package com.luxottica.testautomation.exceptions;

import lombok.Getter;

@Getter
public class UserAttributeException extends RuntimeException {

    public UserAttributeException(String message) {
        super(message);
    }
}
