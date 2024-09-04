package com.luxottica.testautomation.report.enums;

import lombok.Getter;

@Getter
public enum TestStatus {

    PASSED("Passed"),
    PASSED_WITH_MINOR("Passed with minor"),
    BLOCKED("Blocked"),
    FAILED("Failed"),
    NOT_TESTED("Not Tested"),
    NEW_REQUEST("New Request"),
    LABEL_ISSUE("Label issue"),
    MISSING_REQUIREMENT("Missing Requirement"),
    OUT_OF_SCOPE("Out of scope"),
    DISCARDED("Discarded"),
    TO_BE_RETESTED("To be retested");

    private final String status;

    TestStatus(String status) {
        this.status = status;
    }

    public static TestStatus fromString(String status) {
        for (TestStatus testStatus : TestStatus.values()) {
            if (testStatus.status.equalsIgnoreCase(status)) {
                return testStatus;
            }
        }
        return null;
    }
}
