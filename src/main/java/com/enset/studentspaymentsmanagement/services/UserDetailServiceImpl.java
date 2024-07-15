package com.enset.studentspaymentsmanagement.services;

import com.enset.studentspaymentsmanagement.entities.User;

import com.enset.studentspaymentsmanagement.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("user not found");
        }
        User customUser = user.get();
        return org.springframework.security.core.userdetails.User.withUsername(customUser.getUsername())
                .password(customUser.getPassword())
                .roles(String.valueOf(customUser.getRole()))
                .username(customUser.getUsername())
                .authorities(String.valueOf(customUser.getRole().getRoleName()))
                .build();
    }
}
