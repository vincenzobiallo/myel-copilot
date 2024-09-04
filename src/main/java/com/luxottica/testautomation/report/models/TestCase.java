package com.luxottica.testautomation.report.models;

import lombok.Data;

import java.util.Set;
import java.util.TreeSet;

@Data
public class TestCase {

    private String id;
    private String internalId;
    private String macroArea;
    private String area;
    private String summary;

    private String executor;
    private Set<TestStep> steps;

    private boolean failed = false;

    public void addStep(TestStep step) {

        if (steps == null) {
            // TreeSet is used to keep the steps in order (sorted by step number)
            steps = new TreeSet<>();
        }

        steps.add(step);
    }

    public TestStep getStep(int stepNumber) {
        return steps.stream()
                .filter(step -> step.getNumber() == stepNumber)
                .findFirst()
                .orElse(null);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        TestCase testCase = (TestCase) obj;
        return this.getId().equals(testCase.getId());
    }

}
