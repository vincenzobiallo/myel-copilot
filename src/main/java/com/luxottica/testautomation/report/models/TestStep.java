package com.luxottica.testautomation.report.models;

import com.luxottica.testautomation.report.enums.TestStatus;
import lombok.Data;
import org.apache.logging.log4j.util.Strings;

@Data
public class TestStep implements Comparable<TestStep> {

    private Integer number;
    private TestStatus status;
    private String note = Strings.EMPTY;

    public TestStep() {
        this.number = 1;
        this.status = TestStatus.NOT_TESTED;
    }

    public TestStep(Integer number) {

        if (number == null || number < 1) {
            throw new IllegalArgumentException("Test step number must be valind and greater than 0!");
        }

        this.number = number;
        this.status = TestStatus.NOT_TESTED;
    }

    public TestStep(Integer number, TestStatus status) {

        if (number == null || number < 1) {
            throw new IllegalArgumentException("Test step number must be valind and greater than 0!");
        }

        this.number = number;
        this.status = status;
    }

    @Override
    public int hashCode() {
        return number;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        TestStep testStep = (TestStep) obj;
        return number.equals(testStep.number);
    }

    @Override
    public int compareTo(TestStep o) {
        return this.number.compareTo(o.number);
    }
}
