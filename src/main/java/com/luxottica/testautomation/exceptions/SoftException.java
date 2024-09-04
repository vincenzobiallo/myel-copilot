package com.luxottica.testautomation.exceptions;

import com.luxottica.testautomation.report.enums.TestStatus;
import com.microsoft.playwright.PlaywrightException;
import lombok.Getter;

@Getter
public class SoftException extends PlaywrightException {

    private final TestStatus status;

    public SoftException(TestStatus status) {
        super(status.getStatus());
        this.status = status;
    }

}
