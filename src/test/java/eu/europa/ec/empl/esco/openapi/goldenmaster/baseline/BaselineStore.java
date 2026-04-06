package eu.europa.ec.empl.esco.openapi.goldenmaster.baseline;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import eu.europa.ec.empl.esco.openapi.goldenmaster.config.TestConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;

/**
 * Reads and writes baseline JSON files.
 * <p>
 * Path convention: {@code src/test/resources/baselines/{group}/{TestClass}/{testMethod}.json}
 */
public final class BaselineStore {

    private static final Logger LOG = LoggerFactory.getLogger(BaselineStore.class);
    private static final Path BASELINES_ROOT = Path.of("src/test/resources/baselines");

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .enable(SerializationFeature.INDENT_OUTPUT)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private BaselineStore() {
    }

    /**
     * Resolves the baseline file path for a given group, test class, and test method.
     */
    public static Path baselinePath(String group, String testClass, String testMethod) {
        return BASELINES_ROOT.resolve(group).resolve(testClass).resolve(testMethod + ".json");
    }

    /**
     * Returns {@code true} if a baseline file exists for the given coordinates.
     */
    public static boolean exists(String group, String testClass, String testMethod) {
        return Files.exists(baselinePath(group, testClass, testMethod));
    }

    /**
     * Loads a baseline from disk.
     */
    public static Optional<Baseline> load(String group, String testClass, String testMethod) {
        Path path = baselinePath(group, testClass, testMethod);
        if (!Files.exists(path)) {
            return Optional.empty();
        }
        try {
            JsonNode root = MAPPER.readTree(path.toFile());
            Instant capturedAt = Instant.parse(root.get("capturedAt").asText());
            String capturedBaseUrl = root.get("capturedBaseUrl").asText();
            long responseTimeMs = root.get("responseTimeMs").asLong();
            int httpStatus = root.get("httpStatus").asInt();
            JsonNode body = root.get("body");
            return Optional.of(new Baseline(capturedAt, capturedBaseUrl, responseTimeMs, httpStatus, body));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load baseline: " + path, e);
        }
    }

    /**
     * Saves a baseline to disk, creating parent directories as needed.
     *
     * @param group      test group (e.g. "resource", "search")
     * @param testClass  simple class name of the test
     * @param testMethod test method name
     * @param status     HTTP status code
     * @param body       response body (raw string — may or may not be JSON)
     * @param durationMs response time in milliseconds
     */
    public static void save(String group, String testClass, String testMethod,
                            int status, String body, long durationMs) {
        Path path = baselinePath(group, testClass, testMethod);
        try {
            Files.createDirectories(path.getParent());

            ObjectNode root = MAPPER.createObjectNode();
            root.put("capturedAt", Instant.now().toString());
            root.put("capturedBaseUrl", TestConfig.instance().baseUrl());
            root.put("responseTimeMs", durationMs);
            root.put("httpStatus", status);

            // Try to parse body as JSON; fall back to storing as a text node
            JsonNode bodyNode = tryParseJson(body);
            root.set("body", bodyNode);

            MAPPER.writeValue(path.toFile(), root);
            LOG.info("Baseline saved: {}", path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save baseline: " + path, e);
        }
    }

    /**
     * Attempts to parse a string as JSON. If parsing fails, returns a TextNode.
     */
    private static JsonNode tryParseJson(String raw) {
        try {
            return MAPPER.readTree(raw);
        } catch (Exception e) {
            return new TextNode(raw);
        }
    }

    public static ObjectMapper mapper() {
        return MAPPER;
    }
}

