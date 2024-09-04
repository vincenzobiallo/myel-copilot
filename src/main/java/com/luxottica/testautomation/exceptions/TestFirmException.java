package com.luxottica.testautomation.exceptions;

import lombok.Getter;

@Getter
public class TestFirmException extends RuntimeException {

    public TestFirmException(String message) {
        super(message);
    }
}
