package com.luxottica.testautomation.exceptions;

import lombok.Getter;

@Getter
public class BackOfficeUserException extends RuntimeException {

    public BackOfficeUserException(String message) {
        super(message);
    }
}
