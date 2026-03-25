package com.yourcompany.automation.framework.runner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.platform.engine.DiscoverySelector;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourcompany.automation.framework.config.JsonSuiteLoader;
import com.yourcompany.automation.framework.context.TestRunContext;
import com.yourcompany.automation.framework.model.config.ScenarioConfig;
import com.yourcompany.automation.framework.model.execution.ExecutionItem;
import com.yourcompany.automation.framework.model.execution.ScenarioExecutionResult;
import com.yourcompany.automation.framework.model.execution.SuiteExecutionResult;
import com.yourcompany.automation.framework.model.execution.TestMethodResult;
import com.yourcompany.automation.framework.report.JsonReportConsolidator;
import com.yourcompany.automation.framework.writer.ScenarioResultWriter;

/**
 * The {@code JsonTestRunner} class is responsible for running test suites and
 * scenarios defined in a JSON configuration. It builds JUnit 5
 * {@link DiscoverySelector}s from the JSON, executes tests using a
 * {@link Launcher}, collects results into {@link TestMethodResult} objects, and
 * writes per-scenario JSON reports using {@link ScenarioResultWriter}.
 *
 * <p>
 * This class is designed to handle:
 * <ul>
 * <li>Loading test suite configuration from JSON</li>
 * <li>Executing tests method-by-method or full class</li>
 * <li>Generating intermediate per-scenario JSON reports</li>
 * <li>Tracking run ID, suite name, scenario name, class name, and method
 * name</li>
 * </ul>
 */
public class JsonTestRunner {

	/** JUnit 5 Launcher used to execute tests */
	private final Launcher launcher;

	/** Jack son ObjectMapper for JSON serialization (not heavily used here) */
	private final ObjectMapper objectMapper;

	/**
	 * Constructs a new {@code JsonTestRunner} and initializes the JUnit Launcher
	 * and the ObjectMapper.
	 */
	public JsonTestRunner() {
		this.launcher = LauncherFactory.create();
		this.objectMapper = new ObjectMapper();
	}

	/**
	 * Main entry point for running the test runner.
	 * <p>
	 * Loads the test configuration, prints suite structure, builds execution items,
	 * and executes tests scenario by scenario.
	 */
	public void run(String configPath) {
		JsonSuiteLoader.load(configPath);
		Map<String, Map<String, ScenarioConfig>> suiteScenarios = JsonSuiteLoader.SUITE_SCENARIOS;
		printSuiteStructure(suiteScenarios);

		// Build execution items per scenario
		List<ExecutionItem> selectors = buildExecutionItems(suiteScenarios);

		// Execute all scenarios
		execute(selectors);
	}

	/**
	 * Main method to run the test runner from command line.
	 *
	 * @param args command-line arguments (ignored)
	 */
	public static void main(String[] args) {
		// String configPath =
		// "C:\\work\\architecture\\pythingALML\\datasciense\\rohit\\ai-test-automation-framework\\src\\main\\resources\\test-config.json";
		if (args.length == 1) {
			String configPath = args[0];
			JsonTestRunner runner = new JsonTestRunner();
			runner.run(configPath);
		} else {
			System.out.println("Usage: java -jar your.jar <path-to-json>");
		}
	}

	/**
	 * Executes a list of {@link ExecutionItem}s.
	 * <p>
	 * For each scenario:
	 * <ul>
	 * <li>Creates a new {@link ScenarioExecutionResult}</li>
	 * <li>Executes each {@link DiscoverySelector} with a new
	 * {@link SummaryGeneratingListener}</li>
	 * <li>Populates {@link TestMethodResult} for each method/class</li>
	 * <li>Writes the scenario result to JSON using
	 * {@link ScenarioResultWriter}</li>
	 * </ul>
	 *
	 * @param executionItems the list of execution items (one per scenario)
	 */
	private void execute(List<ExecutionItem> executionItems) {
		String runId = TestRunContext.RUN_ID;
		ScenarioResultWriter writer = new ScenarioResultWriter();

		for (ExecutionItem executionItem : executionItems) {
			//
			SuiteExecutionResult suiteExecutionResult = new SuiteExecutionResult();
			ScenarioExecutionResult scenarioExecutionResult = new ScenarioExecutionResult();
			scenarioExecutionResult.setScenarioId(executionItem.getScenarioId());
			scenarioExecutionResult.setScenarioName(executionItem.getScenarioName());
			String suiteName = executionItem.getSuiteName();
			suiteExecutionResult.setSuiteName(suiteName);
			suiteExecutionResult.addScenario(scenarioExecutionResult);
			for (DiscoverySelector selector : executionItem.getSelectors()) {

				// Create a new listener per selector to ensure accurate summary
				SummaryGeneratingListener listener = new SummaryGeneratingListener();

				LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request().selectors(selector)
						.build();

				long startTime = System.currentTimeMillis();
				launcher.execute(request, listener);
				long endTime = System.currentTimeMillis();

				// Convert JUnit summary to TestMethodResult
				TestMethodResult testMethodResult = getTestResults(listener, startTime, endTime);

				// Extract real class and method names from selector
				String className = "UNKNOWN_CLASS";
				String methodName = "UNKNOWN_METHOD";

				if (selector instanceof org.junit.platform.engine.discovery.MethodSelector methodSelector) {
					className = methodSelector.getJavaClass().getName();
					methodName = methodSelector.getMethodName();
				} else if (selector instanceof org.junit.platform.engine.discovery.ClassSelector classSelector) {
					className = classSelector.getJavaClass().getName();
					methodName = "<ALL_METHODS>";
				}

				scenarioExecutionResult.addTestResult(className, methodName, testMethodResult);
			}

			// Write scenario-level JSON report
			writer.write(runId, suiteName, executionItem.getScenarioId(), executionItem.getScenarioName(),
					suiteExecutionResult);
		}
		consolidateReport();
	}

	/**
	 * Builds a list of {@link ExecutionItem}s from the JSON suite configuration.
	 * Each {@link ExecutionItem} contains the suite name, scenario ID, scenario
	 * name, and a list of {@link DiscoverySelector}s for the scenario's classes and
	 * methods.
	 *
	 * @param suiteScenarios the map of suite name to scenario data
	 * @return a list of execution items
	 */
	private List<ExecutionItem> buildExecutionItems(Map<String, Map<String, ScenarioConfig>> suiteScenarios) {
		List<ExecutionItem> executionItems = new ArrayList<>();

		suiteScenarios.forEach((suiteName, scenarioMap) -> {
			scenarioMap.forEach((scenarioId, scenario) -> {

				ExecutionItem item = new ExecutionItem();
				item.setSuiteName(suiteName);
				item.setScenarioId(scenarioId);
				item.setScenarioName(scenario.getScenarioName());

				List<DiscoverySelector> selectors = new ArrayList<>();
				scenario.getClassMethods().forEach((className, methods) -> {
					try {
						ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
						Class<?> clazz = classLoader.loadClass(className);
						if (methods != null && !methods.isEmpty()) {
							methods.forEach(methodName -> {
								System.out.println("Adding method: " + methodName + " from class: " + className);
								selectors.add(DiscoverySelectors.selectMethod(clazz, methodName));
							});
						} else {
							System.out.println("Adding full class: " + className);
							selectors.add(DiscoverySelectors.selectClass(clazz));
						}
					} catch (ClassNotFoundException e) {
						System.err.println("Class not found: " + className);
					}
				});

				item.setSelectors(selectors);
				executionItems.add(item);
			});
		});

		return executionItems;
	}

	/**
	 * Prints the suite, scenario, class, and method structure for debugging
	 * purposes.
	 *
	 * @param items the map of suite name to scenario data
	 */
	private void printSuiteStructure(Map<String, Map<String, ScenarioConfig>> items) {
		items.forEach((suiteName, scenarios) -> {
			System.out.println("Suite: " + suiteName);

			scenarios.forEach((scenarioId, scenario) -> {
				System.out.println("  ScenarioId: " + scenarioId + ", Name: " + scenario.getScenarioName());

				scenario.getClassMethods().forEach((className, methods) -> {
					System.out.println("    Class: " + className);
					methods.forEach(method -> System.out.println("      Method: " + method));
				});
			});
		});
	}

	/**
	 * Converts a JUnit {@link SummaryGeneratingListener} summary to a
	 * {@link TestMethodResult}.
	 *
	 * @param listener  the JUnit listener after test execution
	 * @param startTime the test start time stamp in milliseconds
	 * @param endTime   the test end time stamp in milliseconds
	 * @return a TestMethodResult object with status, start time, and end time
	 */
	private TestMethodResult getTestResults(SummaryGeneratingListener listener, long startTime, long endTime) {
		TestExecutionSummary summary = listener.getSummary();
		TestMethodResult testMethodResult = new TestMethodResult();
		testMethodResult.setStartTime(startTime);
		testMethodResult.setEndTime(endTime);

		if (summary.getTestsFailedCount() > 0) {
			testMethodResult.setStatus(TestMethodResult.Status.FAIL);
		} else if (summary.getTestsSucceededCount() > 0) {
			testMethodResult.setStatus(TestMethodResult.Status.PASS);
		} else {
			testMethodResult.setStatus(TestMethodResult.Status.SKIP);
		}

		return testMethodResult;
	}

	private void consolidateReport() {
		JsonReportConsolidator jcConsolidator = new JsonReportConsolidator();
		String runId = TestRunContext.RUN_ID;
		String folderPath = "target/reports/" + runId;
		String outputFile = "target/reports/" + runId + "/" + System.currentTimeMillis() + ".json";
		try {
			jcConsolidator.consolidate(folderPath, outputFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}