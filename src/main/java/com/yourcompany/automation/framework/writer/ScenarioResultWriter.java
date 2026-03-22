package com.yourcompany.automation.framework.writer;

import java.io.File;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourcompany.automation.framework.model.execution.ScenarioExecutionResult;
import com.yourcompany.automation.framework.model.execution.SuiteExecutionResult;

/**
 * Responsible for writing scenario results to JSON files. Writes per-scenario
 * reports using the format:
 * 
 * <pre>
 *  target/reports/{runId}/{suiteName}/{scenarioId}-{scenarioName}.json
 * </pre>
 */
public class ScenarioResultWriter {

	private final ObjectMapper mapper = new ObjectMapper();

	/**
	 * Writes the scenario result to a JSON file.
	 *
	 * @param runId        unique run ID
	 * @param suiteName    the suite name
	 * @param scenarioId   the scenario ID
	 * @param scenarioName the scenario display name
	 * @param result       the {@link ScenarioExecutionResult} object
	 */
	public void write(String runId, String suiteName, String scenarioId, String scenarioName,
			SuiteExecutionResult result) {
		try {
			String safeSuiteName = suiteName.replaceAll("\\s+", "_");
			String safeScenarioName = scenarioName.replaceAll("\\s+", "_");
			File outFile = new File("target/reports/" + runId + "/" + safeSuiteName + "/" + safeScenarioName + "/"
					+ safeScenarioName + "-" + scenarioId + ".json");
			outFile.getParentFile().mkdirs();
			mapper.writerWithDefaultPrettyPrinter().writeValue(outFile, result);

			System.out.println("Scenario JSON written: " + outFile.getAbsolutePath());
		} catch (Exception e) {
			System.err.println("Failed to write scenario report: " + e.getMessage());
			e.printStackTrace();
		}
	}
}