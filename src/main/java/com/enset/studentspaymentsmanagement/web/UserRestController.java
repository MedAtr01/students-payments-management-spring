package com.enset.studentspaymentsmanagement.web;

import com.enset.studentspaymentsmanagement.auth.PasswordUpdateRequest;
import com.enset.studentspaymentsmanagement.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("web")
@RequiredArgsConstructor
public class UserRestController {
    private final UserService userService;

    @PutMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody PasswordUpdateRequest passwordUpdateRequest) {
        userService.updatePassword(passwordUpdateRequest.getCurrentPassword(), passwordUpdateRequest.getNewPassword());
        return ResponseEntity.ok().build();

    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }
}
