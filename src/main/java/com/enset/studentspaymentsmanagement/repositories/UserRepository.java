package com.enset.studentspaymentsmanagement.repositories;

import com.enset.studentspaymentsmanagement.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findUsersByRoleRoleName(String roleNAme);

    Optional<User> findByUsername(String userName);

    Optional<User> findByEmail(String email);

}
