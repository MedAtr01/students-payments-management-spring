package com.enset.studentspaymentsmanagement.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleStudentDTO {

    private long id;
    private String fullName;
    private String studentCode;
    private String program;
    private String profile;
}
