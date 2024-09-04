package com.luxottica.testautomation.report;

import com.luxottica.testautomation.report.enums.TestStatus;

@FunctionalInterface
public interface TestStepFunction {
    TestStatus apply() throws Exception;
}