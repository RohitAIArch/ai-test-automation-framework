package com.yourcompany.automation.framework.report;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yourcompany.automation.framework.model.execution.TestMethodResult;

public class TestReportGenerator {

    public static void generateReport(Map<String, Map<String, Map<String, List<TestMethodResult>>>> data) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode rootNode = mapper.createObjectNode();

            for (String suite : data.keySet()) {
                ObjectNode suiteNode = mapper.createObjectNode();
                Map<String, Map<String, List<TestMethodResult>>> scenarios = data.get(suite);

                for (String scenario : scenarios.keySet()) {
                    ObjectNode scenarioNode = mapper.createObjectNode();
                    Map<String, List<TestMethodResult>> classes = scenarios.get(scenario);

                    for (String className : classes.keySet()) {
                        scenarioNode.putPOJO(className, classes.get(className));
                    }
                    suiteNode.set(scenario, scenarioNode);
                }
                rootNode.set(suite, suiteNode);
            }

            File output = new File("target/consolidated-test-report.json");
            try (FileWriter writer = new FileWriter(output)) {
                writer.write(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode));
            }

            System.out.println("Consolidated JSON report generated at: " + output.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}