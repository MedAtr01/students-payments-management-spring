package com.enset.studentspaymentsmanagement.DTOs;

import com.enset.studentspaymentsmanagement.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class StudentDTO {

    private long id;
    private String fullName;
    private String studentCode;
    private String program;
    private String profile;
    private UserDTO user;
}
