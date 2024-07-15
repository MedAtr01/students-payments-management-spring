package com.enset.studentspaymentsmanagement.services;

import com.enset.studentspaymentsmanagement.entities.Role;
import com.enset.studentspaymentsmanagement.entities.User;
import com.enset.studentspaymentsmanagement.handler.GlobalExceptionHandler;
import com.enset.studentspaymentsmanagement.repositories.RoleRepository;
import com.enset.studentspaymentsmanagement.repositories.UserRepository;
import com.enset.studentspaymentsmanagement.security.PasswordEncoderConfig;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.logging.Handler;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoderConfig passwordEncoder;
    private final GlobalExceptionHandler handler;


    public User addRoleToUser(String username, String rolename) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        Optional<Role> optionalRole = roleRepository.findByRoleName(rolename);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        if (optionalRole.isEmpty()) {
            throw new RuntimeException("Role not found");
        }


        User user = optionalUser.get();
        user.setRole(optionalRole.get());
        return userRepository.save(user);
    }

    public User addUser(String username, String password, String roleName) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        Optional<Role> optionalRole = roleRepository.findByRoleName(roleName);
        if (optionalRole.isEmpty()) {
            throw new RuntimeException("Role not found");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.passwordEncoder().encode(password));
        user.setRole(optionalRole.get());

        return userRepository.save(user);

    }

    public User getUserByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new RuntimeException("user not found");

        } else
            return user.get();
    }

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationException("User not authenticated") {
            };
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt) {
            Jwt jwtToken = (Jwt) principal;
            String username = jwtToken.getSubject();
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        }

        throw new UsernameNotFoundException("Invalid user details");
    }

    public void updatePassword(String currentPassword, String newPassword) {

        User user = getAuthenticatedUser();
        if (passwordEncoder.passwordEncoder().matches(currentPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.passwordEncoder().encode(newPassword));
            userRepository.save(user);
        } else {
            throw new BadCredentialsException("Password Incorrect");
        }
    }

}

