package com.enset.studentspaymentsmanagement.security;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    @Setter
    private JwtService jwtService;
    private final UserDetailsService userDetailsService;

//    public void setJwtService(JwtService jwtService) {
//        this.jwtService = jwtService;
//    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,@NotNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);
        String username = jwtService.extractUsername(jwt);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if (jwtService.isTokenValid(jwt, userDetails)) {
                // Extracting the claims from the JWT
                Jwt parsedJwt = jwtService.parseJwt(jwt);

                // Converting authorities from UserDetails to a Collection of GrantedAuthority
                Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

                JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(parsedJwt, authorities);
                jwtAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(jwtAuthenticationToken);

                System.out.println("User Authorities: " + userDetails.getAuthorities());
            } else {
                System.out.println("JWT is not valid");
            }
        } else {
            System.out.println("No authentication found in security context or username is null.");
        }

        filterChain.doFilter(request, response);
    }
}


//package com.enset.studentspaymentsmanagement.security;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.Setter;
//import org.springframework.http.HttpHeaders;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.oauth2.jwt.Jwt;
//import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Service;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//
//@Service
//@RequiredArgsConstructor
//public class JwtFilter extends OncePerRequestFilter {
//
//    private JwtService jwtService;
//    private final UserDetailsService userDetailsService;
//
//    // Setter injection for JwtService
//    public void setJwtService(JwtService jwtService) {
//        this.jwtService = jwtService;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain) throws ServletException, IOException {
//        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
//
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        String jwt = authHeader.substring(7);
//        String username = null;
//        try {
//            username = jwtService.extractUsername(jwt);
//        } catch (Exception e) {
//            logger.error("Failed to extract username from JWT: {}");
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
//
//            if (jwtService.isTokenValid(jwt, userDetails)) {
//                try {
//                    Jwt parsedJwt = Jwt.withTokenValue(jwt).build();
//                    JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(parsedJwt, userDetails.getAuthorities());
//                    jwtAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                    SecurityContextHolder.getContext().setAuthentication(jwtAuthenticationToken);
//
//                    System.out.println("User Authorities: " + userDetails.getAuthorities());
//                } catch (IllegalArgumentException e) {
//                    logger.error("JWT creation failed: {}");
//                }
//            } else {
//                System.out.println("JWT is not valid");
//            }
//        } else {
//            System.out.println("No authentication found in security context or username is null.");
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}
