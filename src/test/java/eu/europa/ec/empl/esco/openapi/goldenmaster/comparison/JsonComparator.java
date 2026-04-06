package eu.europa.ec.empl.esco.openapi.goldenmaster.comparison;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Compares a live JSON response against a baseline using JsonUnit.
 * <p>
 * Rules:
 * <ul>
 *   <li><b>Ignore:</b> non-deterministic fields ({@code logref}, {@code timestamp},
 *       {@code eventId}, {@code id}) — stripped before comparison.</li>
 *   <li><b>Normalize:</b> {@code href} values — the deployment base URL is replaced
 *       with a placeholder so that alternative deployments can be validated.</li>
 *   <li><b>Exact match:</b> everything else (including array order)</li>
 * </ul>
 */
public final class JsonComparator {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /** Fields ignored during comparison — server-generated, non-deterministic values. */
    private static final Set<String> IGNORED_FIELDS = Set.of(
            "logref",
            "timestamp",
            "eventId",
            "id"
    );

    /** Placeholder that replaces deployment-specific base URLs in {@code href} values. */
    private static final String BASE_URL_PLACEHOLDER = "{BASE_URL}";

    private JsonComparator() {
    }

    /**
     * Compares an actual JSON response body against the baseline body.
     * {@code href} values are normalized: the baseline's captured base URL and the current
     * base URL are each replaced with a common placeholder before comparison, so that
     * only the path+query portion of links is validated.
     *
     * @param baseline        the baseline JSON body
     * @param actual          the actual (live) JSON body
     * @param baselineBaseUrl the API base URL that was active when the baseline was captured
     * @param actualBaseUrl   the API base URL currently under test
     */
    public static ComparisonResult compare(JsonNode baseline, JsonNode actual,
                                           String baselineBaseUrl, String actualBaseUrl) {
        // If either is a plain text node (non-JSON baseline), do exact string compare
        if (baseline.isTextual() && actual.isTextual()) {
            if (baseline.asText().equals(actual.asText())) {
                return ComparisonResult.ok();
            }
            return ComparisonResult.mismatch(List.of(
                    "Text body mismatch (first 500 chars): expected=["
                            + truncate(baseline.asText(), 500) + "], actual=["
                            + truncate(actual.asText(), 500) + "]"
            ));
        }

        // Normalize both trees: replace base URLs in hrefs, strip non-deterministic fields
        JsonNode normalizedBaseline = normalize(baseline, baselineBaseUrl);
        JsonNode normalizedActual = normalize(actual, actualBaseUrl);

        List<String> differences = new ArrayList<>();

        try {
            JsonAssertions.assertThatJson(normalizedActual.toString())
                    .isEqualTo(normalizedBaseline.toString());
        } catch (AssertionError e) {
            differences.add(e.getMessage());
        }

        if (differences.isEmpty()) {
            return ComparisonResult.ok();
        }
        return ComparisonResult.mismatch(differences);
    }

    /**
     * Compares two raw strings (for non-JSON responses like RDF/Turtle).
     */
    public static ComparisonResult compareRaw(String baseline, String actual) {
        if (baseline.equals(actual)) {
            return ComparisonResult.ok();
        }
        return ComparisonResult.mismatch(List.of(
                "Raw body mismatch (first 500 chars): expected=["
                        + truncate(baseline, 500) + "], actual=["
                        + truncate(actual, 500) + "]"
        ));
    }

    /**
     * Normalizes a JSON tree for comparison:
     * <ul>
     *   <li>Replaces deployment-specific base URLs in {@code href} values with a placeholder.</li>
     *   <li>Strips non-deterministic fields (timestamp, eventId, etc.).</li>
     * </ul>
     * Only allocates new nodes on the path to an actual change — unchanged subtrees are shared.
     */
    static JsonNode normalize(JsonNode node, String baseUrl) {
        if (node.isObject()) {
            ObjectNode copy = MAPPER.createObjectNode();
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String key = entry.getKey();
                // Skip non-deterministic fields entirely
                if (IGNORED_FIELDS.contains(key)) {
                    continue;
                }
                if ("href".equals(key) && entry.getValue().isTextual()) {
                    String value = entry.getValue().asText();
                    if (value.startsWith(baseUrl)) {
                        copy.set(key,
                                new TextNode(BASE_URL_PLACEHOLDER + value.substring(baseUrl.length())));
                    } else {
                        copy.set(key, entry.getValue());
                    }
                } else {
                    copy.set(key, normalize(entry.getValue(), baseUrl));
                }
            }
            return copy;
        } else if (node.isArray()) {
            ArrayNode copy = MAPPER.createArrayNode();
            for (JsonNode child : node) {
                copy.add(normalize(child, baseUrl));
            }
            return copy;
        }
        return node;
    }

    private static String truncate(String s, int maxLen) {
        return s.length() <= maxLen ? s : s.substring(0, maxLen) + "...";
    }
}

