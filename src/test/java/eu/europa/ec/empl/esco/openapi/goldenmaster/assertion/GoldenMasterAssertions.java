package eu.europa.ec.empl.esco.openapi.goldenmaster.assertion;

import com.fasterxml.jackson.databind.JsonNode;
import eu.europa.ec.empl.esco.openapi.goldenmaster.baseline.Baseline;
import eu.europa.ec.empl.esco.openapi.goldenmaster.baseline.BaselineStore;
import eu.europa.ec.empl.esco.openapi.goldenmaster.client.ApiResponse;
import eu.europa.ec.empl.esco.openapi.goldenmaster.comparison.ComparisonResult;
import eu.europa.ec.empl.esco.openapi.goldenmaster.comparison.JsonComparator;
import eu.europa.ec.empl.esco.openapi.goldenmaster.config.TestConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * Orchestrates the golden master pattern: capture-or-compare + status + performance checks.
 * <p>
 * If mode is {@code capture}, baselines are always (re-)saved.
 * Otherwise, baselines are captured when missing and compared when present.
 */
public final class GoldenMasterAssertions {

    private static final Logger LOG = LoggerFactory.getLogger(GoldenMasterAssertions.class);

    private GoldenMasterAssertions() {
    }

    /**
     * Asserts the API response against the golden master baseline.
     *
     * @param group      test group (e.g. "resource", "search")
     * @param testClass  simple class name
     * @param testMethod test method name / test case key
     * @param response   the live API response
     */
    public static void assertGoldenMaster(String group, String testClass, String testMethod,
                                           ApiResponse response) {
        if (shouldCapture(group, testClass, testMethod)) {
            capture(group, testClass, testMethod, response);
        } else {
            compare(group, testClass, testMethod, response);
        }
    }

    /**
     * Asserts the API response matches the expected status code, then delegates to golden master.
     */
    public static void assertGoldenMaster(String group, String testClass, String testMethod,
                                           ApiResponse response, int expectedStatus) {
        assertThat(response.status())
                .as("HTTP status for %s/%s/%s\n  URI: %s\n  body: %s",
                        group, testClass, testMethod, response.uri(), response.body())
                .isEqualTo(expectedStatus);
        assertGoldenMaster(group, testClass, testMethod, response);
    }

    private static boolean shouldCapture(String group, String testClass, String testMethod) {
        return "capture".equals(TestConfig.instance().mode())
                || !BaselineStore.exists(group, testClass, testMethod);
    }

    private static void capture(String group, String testClass, String testMethod,
                                ApiResponse response) {
        LOG.info("CAPTURE mode: saving baseline for {}/{}/{}", group, testClass, testMethod);
        BaselineStore.save(group, testClass, testMethod,
                response.status(), response.body(), response.durationMs());
    }

    private static void compare(String group, String testClass, String testMethod,
                                ApiResponse response) {
        String testId = "%s/%s/%s".formatted(group, testClass, testMethod);
        Baseline baseline = BaselineStore.load(group, testClass, testMethod)
                .orElseThrow(() -> new IllegalStateException("No baseline found for " + testId));

        // 1. Status code must match exactly
        assertThat(response.status())
                .as("HTTP status mismatch for %s\n  URI: %s\n  body: %s",
                        testId, response.uri(), response.body())
                .isEqualTo(baseline.httpStatus());

        // 2. Body comparison
        LOG.info("Comparing baseline for {} …", testId);
        long start = System.currentTimeMillis();
        JsonNode actualBody = parseBody(response.body());
        ComparisonResult result = JsonComparator.compare(
                baseline.body(), actualBody,
                baseline.capturedBaseUrl(), TestConfig.instance().baseUrl());
        long compareMs = System.currentTimeMillis() - start;
        LOG.info("Comparison done for {} ({} ms)", testId, compareMs);
        if (!result.match()) {
            String prettyActual;
            String prettyExpected;
            try {
                var writer = BaselineStore.mapper().writerWithDefaultPrettyPrinter();
                prettyActual = writer.writeValueAsString(actualBody);
                prettyExpected = writer.writeValueAsString(baseline.body());
            } catch (Exception e) {
                prettyActual = response.body();
                prettyExpected = baseline.body().toString();
            }
            fail("Body mismatch for %s/%s/%s\n\n── Request ──\n  URI: %s\n  HTTP status: %d\n\n── Differences ──\n%s\n\n── Expected (baseline) ──\n%s\n\n── Actual (HTTP response) ──\n%s",
                    group, testClass, testMethod,
                    response.uri(), response.status(),
                    String.join("\n", result.differences()),
                    prettyExpected, prettyActual);
        }

        // 3. Performance check
        int thresholdPct = TestConfig.instance().performanceThresholdPct();
        long maxAllowed = baseline.responseTimeMs() * (100 + thresholdPct) / 100;
        if (response.durationMs() > maxAllowed) {
            LOG.warn("PERFORMANCE REGRESSION for {}/{}/{}: baseline={}ms, actual={}ms, threshold={}%",
                    group, testClass, testMethod,
                    baseline.responseTimeMs(), response.durationMs(), thresholdPct);
        }
    }

    private static JsonNode parseBody(String body) {
        try {
            return BaselineStore.mapper().readTree(body);
        } catch (Exception e) {
            // Not valid JSON — wrap as text node
            return BaselineStore.mapper().getNodeFactory().textNode(body);
        }
    }
}
