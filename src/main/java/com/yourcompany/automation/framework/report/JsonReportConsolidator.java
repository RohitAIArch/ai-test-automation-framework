package com.yourcompany.automation.framework.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.yourcompany.automation.framework.model.execution.ScenarioExecutionResult;
import com.yourcompany.automation.framework.model.execution.SuiteExecutionResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class JsonReportConsolidator {

    private final ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    public void consolidate(String folderPath, String outputFile) throws IOException {

        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            throw new RuntimeException("Folder does not exist: " + folderPath);
        }

        List<File> files = getAllJsonFiles(folderPath);

        // 🔥 Group by suiteName
        Map<String, SuiteExecutionResult> suiteMap = new LinkedHashMap<>();

        for (File file : files) {

            // safety check
            if (!file.getName().toLowerCase().endsWith(".json")) continue;

            SuiteExecutionResult inputSuite =
                    mapper.readValue(file, SuiteExecutionResult.class);

            String suiteName = inputSuite.getSuiteName();

            // ✅ create or get existing suite
            SuiteExecutionResult finalSuite =
                    suiteMap.computeIfAbsent(suiteName, k -> {
                        SuiteExecutionResult s = new SuiteExecutionResult();
                        s.setSuiteName(suiteName);
                        return s;
                    });

            // ensure list is initialized
            if (finalSuite.getScenarios() == null) {
                finalSuite.setScenarios(new ArrayList<>());
            }

            // 🔥 merge scenarios
            for (ScenarioExecutionResult incoming : inputSuite.getScenarios()) {

                Optional<ScenarioExecutionResult> existingOpt =
                        finalSuite.getScenarios().stream()
                                .filter(s -> s.getScenarioId().equals(incoming.getScenarioId()))
                                .findFirst();

                if (existingOpt.isEmpty()) {
                    finalSuite.getScenarios().add(incoming);
                } 
//                else { I don't have merge scenario.
//                    mergeScenario(existingOpt.get(), incoming);
//                }
            }
        }

        // ✅ Ensure output directory exists
        File outFile = new File(outputFile);
        File parent = outFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        // 🔥 FINAL WRITE (OUTSIDE LOOP)
        Map<String, Object> finalOutput = new LinkedHashMap<>();
        finalOutput.put("suites", suiteMap.values());

        mapper.writeValue(outFile, finalOutput);

        System.out.println("✅ Consolidated report generated: " + outputFile);
    }

    // 🔥 Merge logic for same scenario across files
//    private void mergeScenario(ScenarioExecutionResult existing,
//                               ScenarioExecutionResult incoming) {
//
//        existing.setTotalTests(existing.getTotalTests() + incoming.getTotalTests());
//        existing.setPassed(existing.getPassed() + incoming.getPassed());
//        existing.setFailed(existing.getFailed() + incoming.getFailed());
//        existing.setSkipped(existing.getSkipped() + incoming.getSkipped());
//
//        if (existing.getResults() == null) {
//            existing.setResults(new LinkedHashMap<>());
//        }
//
//        incoming.getResults().forEach((className, methods) -> {
//            existing.getResults()
//                    .computeIfAbsent(className, k -> new LinkedHashMap<>())
//                    .putAll(methods);
//        });
//    }

    // 🔥 Recursive JSON file scanner
    private List<File> getAllJsonFiles(String folderPath) {

        try {
            return Files.walk(Paths.get(folderPath))
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().toLowerCase().endsWith(".json"))
                    .map(Path::toFile)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            throw new RuntimeException("Error scanning folder: " + folderPath, e);
        }
    }
}