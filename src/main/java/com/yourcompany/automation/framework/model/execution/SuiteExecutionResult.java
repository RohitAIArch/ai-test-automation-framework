package com.yourcompany.automation.framework.model.execution;

import java.util.ArrayList;
import java.util.List;

public class SuiteExecutionResult {

    private String suiteName;

	/**
	 * @return the suiteName
	 */
	public String getSuiteName() {
		return suiteName;
	}

	/**
	 * @param suiteName the suiteName to set
	 */
	public void setSuiteName(String suiteName) {
		this.suiteName = suiteName;
	}

	/**
	 * @return the scenarios
	 */
	public List<ScenarioExecutionResult> getScenarios() {
		return scenarios;
	}

	/**
	 * @param scenarios the scenarios to set
	 */
	public void setScenarios(List<ScenarioExecutionResult> scenarios) {
		this.scenarios = scenarios;
	}

	private List<ScenarioExecutionResult> scenarios = new ArrayList<>();

    public void addScenario(ScenarioExecutionResult scenario) {
        scenarios.add(scenario);
    }

}
