package com.enset.studentspaymentsmanagement.auth;


import com.enset.studentspaymentsmanagement.services.UserDetailServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class registrationRestController {
    private final RegistrationService registrationService;
    private final UserDetailServiceImpl userService;


    @PostMapping("/register")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> register(@RequestBody @Valid RegistrationRequest registrationRequest) throws Exception {
        registrationService.register(registrationRequest);
        return ResponseEntity.accepted().build();
    }


    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody @Valid AuthenticationRequest request) {
        return ResponseEntity.ok(registrationService.authenticate(request));
    }

    @PostMapping("/activate-account")
    public void confirm(@RequestParam String token) throws MessagingException {
        registrationService.activateAccount(token);
    }

    @GetMapping("load-user")
    public UserDetails loadUser(UserDetails userDetails) {
        String username = userDetails.getUsername();
        return userService.loadUserByUsername(username);

    }


//    @PostMapping(value = "/registration-completion")
//    public ResponseEntity<?> registerCompletion(@AuthenticationPrincipal Jwt jwt,
//                                                @RequestParam("profile") MultipartFile profile,
//                                                @RequestParam("studentCode") String studentCode,
//                                                @RequestParam("program") String program
//    ) throws IOException {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null || !authentication.isAuthenticated()) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
//        }
//        Object principal = authentication.getPrincipal();
//
//        Student createdStudent;
//        if (principal instanceof Jwt) {
//            Jwt jwtToken = (Jwt) principal;
//            String username = jwtToken.getSubject();
//
//            Optional<User> user = userRepository.findByUsername(username);
//            if (user.isEmpty()) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
//            }
//            createdStudent = registrationService.createStudent(user.get(), profile, studentCode, program);
//            return new ResponseEntity<>(createdStudent, HttpStatus.CREATED);
//
//        } else {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user details");
//        }
//    }


}
