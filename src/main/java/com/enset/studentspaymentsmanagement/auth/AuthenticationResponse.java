package com.enset.studentspaymentsmanagement.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Setter
@Builder
public class AuthenticationResponse {
    private String token;
//    private UserDetails userDetails;
}
