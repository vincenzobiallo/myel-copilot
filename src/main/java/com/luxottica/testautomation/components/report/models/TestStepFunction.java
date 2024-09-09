package com.luxottica.testautomation.components.report.models;

import com.luxottica.testautomation.components.report.enums.TestStatus;

@FunctionalInterface
public interface TestStepFunction {
    TestStatus apply() throws Exception;
}