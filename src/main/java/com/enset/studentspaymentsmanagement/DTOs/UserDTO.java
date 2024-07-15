package com.enset.studentspaymentsmanagement.DTOs;

import com.enset.studentspaymentsmanagement.entities.Role;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UserDTO {
    private long userId;
    private String username;
    private String email;
    private String password;
    private boolean accountLocked;
    private boolean enabled;
    private Role role;
}
