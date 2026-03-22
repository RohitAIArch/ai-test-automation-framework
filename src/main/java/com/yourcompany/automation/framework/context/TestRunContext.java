package com.yourcompany.automation.framework.context;

import java.util.*;

import com.yourcompany.automation.framework.model.execution.TestMethodResult;

/**
 * Holds the context for a single test run.
 * <p>
 * Provides a globally unique {@link #RUN_ID} for the current test execution
 * and a centralized in-memory storage {@link #DATA} for storing test results
 * by suite, scenario, and class/method hierarchy.
 * </p>
 *
 * <p>Usage example:</p>
 * <pre>
 * String runId = TestRunContext.RUN_ID;
 * Map<String, Map<String, Map<String, List&lt;TestMethodResult&gt;&gt;&gt; results = TestRunContext.DATA;
 * </pre>
 */
public class TestRunContext {

    /**
     * Unique identifier for the current test run.
     * <p>
     * By default, uses system property "runId" if provided, otherwise
     * falls back to the current system time in milliseconds.
     * </p>
     */
    public static final String RUN_ID =
            System.getProperty("runId", String.valueOf(System.currentTimeMillis()));

    /**
     * Centralized in-memory storage for all test results of the current run.
     * <p>
     * Structure:
     * <pre>
     *  Map&lt;suiteName, Map&lt;scenarioId, Map&lt;className, List&lt;TestMethodResult&gt;&gt;&gt;&gt;
     * </pre>
     * This allows organizing test results by suite, scenario, and test class/method.
     * </p>
     */
    public static final Map<String, Map<String, Map<String, List<TestMethodResult>>>> DATA = new HashMap<>();
}