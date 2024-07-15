package com.enset.studentspaymentsmanagement.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegistrationRequest {
    @NotEmpty(message = "field is mandatory")
    @NotBlank(message = "field is mandatory")
    private String fullName;
    @NotEmpty(message = "field is mandatory")
    @NotBlank(message = "field is mandatory")
    private String username;
    @NotEmpty(message = "field is mandatory")
    @NotBlank(message = "field is mandatory")
    @Email(message = "Respect email format")
    private String email;
    @NotEmpty(message = "field is mandatory")
    @NotBlank(message = "field is mandatory")
    @Size(min = 8, message = "at least 8 chars")
    private String password;
    @NotEmpty(message = "field is mandatory")
    @NotBlank(message = "field is mandatory")
    private String studentCode;
    @NotEmpty(message = "field is mandatory")
    @NotBlank(message = "field is mandatory")
    private String program;


}
