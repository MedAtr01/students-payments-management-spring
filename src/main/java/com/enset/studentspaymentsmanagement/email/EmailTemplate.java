package com.enset.studentspaymentsmanagement.email;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmailTemplate {
    ACTIVATE_ACCOUNT("activate_account");

    private final String name;
}
