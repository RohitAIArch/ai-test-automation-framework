package com.yourcompany.automation.framework.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourcompany.automation.framework.model.config.ScenarioConfig;

/**
 * Loader for JSON test suite configuration.
 * <p>
 * This class reads a JSON file (by default `test-config.json` in classpath) and
 * populates a hierarchical structure of test suites, scenarios, classes, and
 * methods. The loaded structure is stored in {@link #SUITE_SCENARIOS}.
 * </p>
 *
 * <p>
 * Structure of {@link #SUITE_SCENARIOS}:
 * 
 * <pre>
 * SuiteName → ScenarioId → ScenarioConfig
 * </pre>
 * 
 * where {@link ScenarioConfig} contains scenarioId, scenarioName, and
 * class-method mapping.
 * </p>
 *
 * <p>
 * Validations performed during loading:
 * <ul>
 * <li>Suite names must be globally unique</li>
 * <li>Scenario IDs must be globally unique</li>
 * <li>Class names within a scenario must be unique</li>
 * <li>Each class must contain at least one method</li>
 * </ul>
 * </p>
 */
public class JsonSuiteLoader {

	/**
	 * Loaded suite-scenario structure.
	 * <p>
	 * Map format: SuiteName → ScenarioId → {@link ScenarioConfig}
	 * </p>
	 */
	public static final Map<String, Map<String, ScenarioConfig>> SUITE_SCENARIOS = new LinkedHashMap<>();

	/** Used to validate uniqueness of suite names */
	private static final Set<String> suiteNames = new LinkedHashSet<>();

	/** Used to validate uniqueness of scenario IDs */
	private static final Set<String> scenarioIds = new LinkedHashSet<>();

	/**
	 * Loads the JSON configuration from classpath and populates
	 * {@link #SUITE_SCENARIOS}.
	 * <p>
	 * This method also validates the structure for duplicate suites, scenario IDs,
	 * duplicate classes within a scenario, and presence of methods.
	 * </p>
	 *
	 * @throws RuntimeException if JSON is missing, malformed, or validation fails
	 */
	public static void load(String configPath) {
		
		JsonNode root = null;
		try {
			// TODO: Remove hard-coded filename or make configurable
			try {
			    String json = Files.readString(Path.of(configPath));
		        ObjectMapper mapper = new ObjectMapper();
		        root = mapper.readTree(json);
			} catch (IOException e) {
			    throw new RuntimeException("Failed to load config: " + configPath, e);
			}

			validateJsonStructure(root);

			for (JsonNode suiteNode : root.path("suites")) {
				String suiteName = getText(suiteNode, "suiteName", "Suite");

				Map<String, ScenarioConfig> scenarioMap = new LinkedHashMap<>();

				for (JsonNode scenarioNode : suiteNode.path("scenarios")) {
					String scenarioId = getText(scenarioNode, "scenarioId", "Scenario");
					String scenarioName = getText(scenarioNode, "scenarioName", "Scenario");

					Map<String, Set<String>> classMap = new LinkedHashMap<>();
					Set<String> classNames = new LinkedHashSet<>();

					for (JsonNode cls : scenarioNode.path("classes")) {
						String className = getText(cls, "className", "Class");

						if (!classNames.add(className)) {
							throw new RuntimeException(
									"Duplicate class '" + className + "' in scenarioId '" + scenarioId + "'");
						}

						Set<String> methods = new LinkedHashSet<>();
						JsonNode methodNodes = cls.path("methods");

						if (!methodNodes.isArray() || methodNodes.isEmpty()) {
							throw new RuntimeException(
									"No methods for class '" + className + "' in scenarioId '" + scenarioId + "'");
						}

						for (JsonNode m : methodNodes) {
							String methodName = m.asText().trim();
							if (methodName.isEmpty()) {
								throw new RuntimeException("Empty method in class '" + className + "'");
							}
							methods.add(methodName);
						}

						classMap.put(className, methods);
					}

					ScenarioConfig scenarioData = new ScenarioConfig(scenarioId, scenarioName, classMap);
					scenarioMap.put(scenarioId, scenarioData);
				}

				SUITE_SCENARIOS.put(suiteName, scenarioMap);
			}

		} catch (Exception e) {
			throw new RuntimeException("Failed to load JSON: " + e.getMessage(), e);
		}
	}

	/**
	 * Validates the JSON structure for uniqueness of suite names, scenario IDs, and
	 * class names.
	 *
	 * @param root Root node of the parsed JSON
	 * @throws RuntimeException if duplicates are found
	 */
	private static void validateJsonStructure(JsonNode root) {
		suiteNames.clear();
		scenarioIds.clear();

		for (JsonNode suiteNode : root.path("suites")) {
			String suiteName = getText(suiteNode, "suiteName", "Suite");

			if (!suiteNames.add(suiteName)) {
				throw new RuntimeException("Duplicate suiteName: " + suiteName);
			}

			for (JsonNode scenarioNode : suiteNode.path("scenarios")) {
				String scenarioId = getText(scenarioNode, "scenarioId", "Scenario");

				if (!scenarioIds.add(scenarioId)) {
					throw new RuntimeException("Duplicate scenarioId globally: " + scenarioId);
				}

				Set<String> classNames = new LinkedHashSet<>();
				for (JsonNode cls : scenarioNode.path("classes")) {
					String className = getText(cls, "className", "Class");

					if (!classNames.add(className)) {
						throw new RuntimeException(
								"Duplicate class '" + className + "' in scenarioId '" + scenarioId + "'");
					}
				}
			}
		}
	}

	/**
	 * Helper method to extract and validate a text field from a JSON node.
	 *
	 * @param node  JSON node
	 * @param field Field name to extract
	 * @param type  Human-readable type for error messages
	 * @return trimmed text value
	 * @throws RuntimeException if field is missing or empty
	 */
	private static String getText(JsonNode node, String field, String type) {
		JsonNode value = node.get(field);

		if (value == null || value.asText().trim().isEmpty()) {
			throw new RuntimeException(type + " missing/empty field: " + field);
		}

		return value.asText().trim();
	}
}