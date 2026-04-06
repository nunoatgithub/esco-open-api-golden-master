package eu.europa.ec.empl.esco.openapi.goldenmaster.comparison;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import net.javacrumbs.jsonunit.core.Option;

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
 *   <li><b>Ignore:</b> {@code logref}</li>
 *   <li><b>Normalize:</b> {@code href} values — the deployment base URL is replaced
 *       with a placeholder so that alternative deployments can be validated.</li>
 *   <li><b>Lenient array order:</b> arrays in {@code _embedded.*}</li>
 *   <li><b>Exact match:</b> everything else</li>
 * </ul>
 */
public final class JsonComparator {

    private static final Set<String> IGNORED_PATHS = Set.of(
            "logref"
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

        // Normalize href values in both trees before comparison
        JsonNode normalizedBaseline = normalizeHrefs(baseline, baselineBaseUrl);
        JsonNode normalizedActual = normalizeHrefs(actual, actualBaseUrl);

        List<String> differences = new ArrayList<>();

        try {
            var assertion = JsonAssertions.assertThatJson(normalizedActual.toString())
                    .when(Option.IGNORING_ARRAY_ORDER);

            for (String path : IGNORED_PATHS) {
                assertion = assertion.whenIgnoringPaths(path);
            }

            assertion.isEqualTo(normalizedBaseline.toString());
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
     * Returns a deep copy of the given JSON tree where every {@code "href"} field
     * whose string value starts with {@code baseUrl} has that prefix replaced
     * with {@link #BASE_URL_PLACEHOLDER}.
     */
    static JsonNode normalizeHrefs(JsonNode node, String baseUrl) {
        if (node.isObject()) {
            ObjectNode copy = node.deepCopy();
            Iterator<Map.Entry<String, JsonNode>> fields = copy.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                if ("href".equals(entry.getKey()) && entry.getValue().isTextual()) {
                    String value = entry.getValue().asText();
                    if (value.startsWith(baseUrl)) {
                        copy.set(entry.getKey(),
                                new TextNode(BASE_URL_PLACEHOLDER + value.substring(baseUrl.length())));
                    }
                } else {
                    copy.set(entry.getKey(), normalizeHrefs(entry.getValue(), baseUrl));
                }
            }
            return copy;
        } else if (node.isArray()) {
            ArrayNode copy = node.deepCopy();
            for (int i = 0; i < copy.size(); i++) {
                copy.set(i, normalizeHrefs(copy.get(i), baseUrl));
            }
            return copy;
        }
        return node;
    }

    private static String truncate(String s, int maxLen) {
        return s.length() <= maxLen ? s : s.substring(0, maxLen) + "...";
    }
}

