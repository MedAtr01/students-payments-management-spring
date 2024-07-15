package com.enset.studentspaymentsmanagement.repositories;

import com.enset.studentspaymentsmanagement.entities.Token;
import com.enset.studentspaymentsmanagement.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {
    Optional<Token> findByToken(String token);

    Optional<Token> findByUser(User user);
}
