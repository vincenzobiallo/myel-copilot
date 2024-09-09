package com.luxottica.testautomation.exceptions;

import lombok.Getter;

@Getter
public class ContextMissingValueException extends RuntimeException {

    public ContextMissingValueException(String message) {
        super(message);
    }
}
