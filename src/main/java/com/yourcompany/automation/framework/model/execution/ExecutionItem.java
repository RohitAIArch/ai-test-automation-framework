package com.yourcompany.automation.framework.model.execution;

import java.util.ArrayList;
import java.util.List;

import org.junit.platform.engine.DiscoverySelector;

public class ExecutionItem {

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
	 * @return the scenarioId
	 */
	public String getScenarioId() {
		return scenarioId;
	}

	/**
	 * @param scenarioId the scenarioId to set
	 */
	public void setScenarioId(String scenarioId) {
		this.scenarioId = scenarioId;
	}

	/**
	 * @return the scenarioName
	 */
	public String getScenarioName() {
		return scenarioName;
	}

	/**
	 * @param scenarioName the scenarioName to set
	 */
	public void setScenarioName(String scenarioName) {
		this.scenarioName = scenarioName;
	}

	/**
	 * @param selectors the selectors to set
	 */
	public void setSelectors(List<DiscoverySelector> selectors) {
		this.selectors = selectors;
	}

	private String scenarioId;
    private String scenarioName;

    private List<DiscoverySelector> selectors = new ArrayList<DiscoverySelector>();

    // getters/setters

    public void addSelector(DiscoverySelector selector) {
        this.selectors.add(selector);
    }

    public List<DiscoverySelector> getSelectors() {
        return selectors;
    }
}