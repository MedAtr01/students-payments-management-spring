package com.enset.studentspaymentsmanagement.exception;

import com.enset.studentspaymentsmanagement.handler.BusinessErrorCodes;
import lombok.Getter;


@Getter
public class EmailAlreadyExistsException extends RuntimeException {
    private final BusinessErrorCodes errorCode;

    public EmailAlreadyExistsException(BusinessErrorCodes errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
