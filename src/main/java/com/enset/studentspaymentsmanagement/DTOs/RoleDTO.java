package com.enset.studentspaymentsmanagement.DTOs;

import com.enset.studentspaymentsmanagement.entities.User;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO {
    private long roleId;
    private String roleName;
}
