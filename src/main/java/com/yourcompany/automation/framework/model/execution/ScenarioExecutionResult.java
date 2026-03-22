package com.yourcompany.automation.framework.model.execution;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents the results of a single test scenario.
 * <p>
 * Stores all executed test results at the method level, organized by class and method name.
 * Also maintains counters for total, passed, failed, and skipped tests to allow quick summary.
 * </p>
 *
 * <p>Usage example:</p>
 * <pre>
 * ScenarioExecutionResult result = new ScenarioExecutionResult();
 * result.setScenarioId("SCN_001");
 * result.setScenarioName("User API Tests");
 * result.addTestResult("com.example.MyTestClass", "testMethod1", testResult);
 * System.out.println("Passed tests: " + result.getPassed());
 * </pre>
 */
public class ScenarioExecutionResult {

    /** Unique identifier for the scenario */
    private String scenarioId;

    /** Display name of the scenario */
    private String scenarioName;

    /** Total number of test results added */
    private int totalTests;

    /** Number of tests that passed */
    private int passed;

    /** Number of tests that failed */
    private int failed;

    /** Number of tests that were skipped */
    private int skipped;

    /** Map of results: Class Name → Method Name → {@link TestMethodResult} */
    private Map<String, Map<String, TestMethodResult>> results = new LinkedHashMap<>();

    // =========================
    // Add Test Result
    // =========================

    /**
     * Adds a test result for a specific class and method.
     * Automatically updates the counters for total, passed, failed, and skipped.
     *
     * @param className  Fully-qualified class name of the test
     * @param methodName Name of the test method
     * @param result     {@link TestMethodResult} containing the test status and execution time
     */
    public void addTestResult(String className, String methodName, TestMethodResult result) {
        results.computeIfAbsent(className, k -> new LinkedHashMap<>()).put(methodName, result);

        totalTests++;

        switch (result.getStatus()) {
            case PASS:
                passed++;
                break;
            case FAIL:
                failed++;
                break;
            case SKIP:
                skipped++;
                break;
        }
    }

    // =========================
    // Getters & Setters
    // =========================

    /**
     * Gets the scenario ID.
     *
     * @return scenarioId
     */
    public String getScenarioId() {
        return scenarioId;
    }

    /**
     * Sets the scenario ID.
     *
     * @param scenarioId unique identifier for the scenario
     */
    public void setScenarioId(String scenarioId) {
        this.scenarioId = scenarioId;
    }

    /**
     * Gets the scenario display name.
     *
     * @return scenarioName
     */
    public String getScenarioName() {
        return scenarioName;
    }

    /**
     * Sets the scenario display name.
     *
     * @param scenarioName human-readable scenario name
     */
    public void setScenarioName(String scenarioName) {
        this.scenarioName = scenarioName;
    }

    /**
     * Gets the total number of test results added.
     *
     * @return total test count
     */
    public int getTotalTests() {
        return totalTests;
    }

    /**
     * Gets the number of tests that passed.
     *
     * @return passed test count
     */
    public int getPassed() {
        return passed;
    }

    /**
     * Gets the number of tests that failed.
     *
     * @return failed test count
     */
    public int getFailed() {
        return failed;
    }

    /**
     * Gets the number of tests that were skipped.
     *
     * @return skipped test count
     */
    public int getSkipped() {
        return skipped;
    }

    /**
     * Gets the full map of test results organized by class and method.
     *
     * @return nested map: Class Name → Method Name → {@link TestMethodResult}
     */
    public Map<String, Map<String, TestMethodResult>> getResults() {
        return results;
    }

    /**
     * Sets the full map of test results. Use with caution as it may override counters.
     *
     * @param results nested map: Class Name → Method Name → {@link TestMethodResult}
     */
    public void setResults(Map<String, Map<String, TestMethodResult>> results) {
        this.results = results;
    }
}