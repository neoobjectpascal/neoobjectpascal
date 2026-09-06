package com.neoobjectpascal;

public class TestResult {
    private final String testName;
    private boolean passed;
    private String errorMessage;

    public TestResult(String testName) {
        this.testName = testName;
        this.passed = false;
        this.errorMessage = null;
    }

    public String getTestName() {
        return testName;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        if (passed) {
            return "[PASS] " + testName;
        } else {
            return "[FAIL] " + testName + (errorMessage != null ? ": " + errorMessage : "");
        }
    }
}
