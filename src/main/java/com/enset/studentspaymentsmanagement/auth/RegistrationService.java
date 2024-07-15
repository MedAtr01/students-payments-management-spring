package com.enset.studentspaymentsmanagement.auth;

import com.enset.studentspaymentsmanagement.email.EmailService;
import com.enset.studentspaymentsmanagement.email.EmailTemplate;
import com.enset.studentspaymentsmanagement.entities.Student;
import com.enset.studentspaymentsmanagement.entities.Token;
import com.enset.studentspaymentsmanagement.entities.User;
import com.enset.studentspaymentsmanagement.exception.EmailAlreadyExistsException;
import com.enset.studentspaymentsmanagement.handler.BusinessErrorCodes;
import com.enset.studentspaymentsmanagement.repositories.RoleRepository;
import com.enset.studentspaymentsmanagement.repositories.StudentRepository;
import com.enset.studentspaymentsmanagement.repositories.TokenRepository;
import com.enset.studentspaymentsmanagement.repositories.UserRepository;
import com.enset.studentspaymentsmanagement.security.JwtService;
import com.enset.studentspaymentsmanagement.security.PasswordEncoderConfig;
import com.enset.studentspaymentsmanagement.services.UserService;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final RoleRepository roleRepository;
    private final UserService userService;
    private final PasswordEncoderConfig passwordEncoder;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final StudentRepository studentRepository;


    @Value("${application.mailing.frontend.activation-url}")
    private String confimationUrl;

    public void register(RegistrationRequest registrationRequest) throws Exception {

        var userRole = roleRepository.findByRoleName("USER")
                //todo - better error handling
                .orElseThrow(() -> new IllegalStateException("role not found"));
        String username = registrationRequest.getUsername();
        String email = registrationRequest.getEmail();

        if (userRepository.findByEmail(email).isPresent()) {
            throw new EmailAlreadyExistsException(BusinessErrorCodes.ACCOUNT_ALREADY_EXIST, "Email already exists");
        }
        if (userRepository.findByUsername(username).isPresent()) {
            throw new Exception("username already exist");

        }
        var user = User.builder()

                .username(registrationRequest.getUsername())
                .email(registrationRequest.getEmail())
                .password(passwordEncoder.passwordEncoder().encode(registrationRequest.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .role(userRole)
                .build();
        userRepository.save(user);
        var student = Student.builder()
                .fullName(registrationRequest.getFullName())
                .studentCode(registrationRequest.getStudentCode())
                .program(registrationRequest.getProgram())
                .user(user)
                .build();
        studentRepository.save(student);


        sendValidationEmail(user);

    }

    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);
        emailService.sendEmail(user.getEmail()
                , user.getUsername()
                , EmailTemplate.ACTIVATE_ACCOUNT
                , confimationUrl
                , newToken
                , "Account activation");
    }

    private String generateAndSaveActivationToken(User user) {
        // Generate a new activation code
        String generatedToken = generateActivationCode(6);

        // Check if the user already has an existing token
        Optional<Token> existingTokenOpt = tokenRepository.findByUser(user);

        // If an existing token is found, update it; otherwise, create a new token
        Token token;
        if (existingTokenOpt.isPresent()) {
            token = existingTokenOpt.get();
            token.setToken(generatedToken);
            token.setCreatedAt(LocalDateTime.now());
            token.setExpiresAt(LocalDateTime.now().plusMinutes(20));
        } else {
            token = Token.builder()
                    .token(generatedToken)
                    .createdAt(LocalDateTime.now())
                    .expiresAt(LocalDateTime.now().plusMinutes(20))
                    .user(user)
                    .build();
        }

        // Save the token (updates if it already exists)
        tokenRepository.save(token);

        return generatedToken;
    }


    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }
        return codeBuilder.toString();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            var auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()));
            var claims = new HashMap<String, Object>();
            var user = ((UserDetails) auth.getPrincipal());
            com.enset.studentspaymentsmanagement.entities.User customUser = userRepository.findByUsername(user.getUsername()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
            claims.put("username", customUser.getUsername());
            claims.put("email", customUser.getEmail());
            var jwtToken = jwtService.generateToken(user);
            SecurityContextHolder.getContext().setAuthentication(auth);
            System.out.println(SecurityContextHolder.getContext().getAuthentication());
            return AuthenticationResponse.builder()
                    .token(jwtToken.get("jwt"))
                    .build();
        } catch (UsernameNotFoundException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    @Transactional
    public void activateAccount(String token) throws MessagingException {
        Token savedToken = tokenRepository.findByToken(token).orElseThrow(() -> new MessagingException("Invalid token"));
        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            sendValidationEmail(savedToken.getUser());
            throw new MessagingException("Activation expired. A new token sent to your email");
        }
        var user = userRepository.findById(savedToken.getUser().getUserId()).orElseThrow(() -> new UsernameNotFoundException("username not found"));
        user.setEnabled(true);
        userRepository.save(user);
        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }


}
