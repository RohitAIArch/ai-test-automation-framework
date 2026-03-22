package com.yourcompany.automation.framework.model.config;

import java.util.Map;
import java.util.Set;

public class ScenarioConfig {

    private final String scenarioId;
    private final String scenarioName;
    private final Map<String, Set<String>> classMethods;

    public ScenarioConfig(String scenarioId, String scenarioName,
                        Map<String, Set<String>> classMethods) {
        this.scenarioId = scenarioId;
        this.scenarioName = scenarioName;
        this.classMethods = classMethods;
    }

    public String getScenarioId() {
        return scenarioId;
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public Map<String, Set<String>> getClassMethods() {
        return classMethods;
    }
}