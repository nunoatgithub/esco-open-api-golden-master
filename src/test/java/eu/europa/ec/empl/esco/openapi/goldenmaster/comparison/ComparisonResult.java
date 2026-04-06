package eu.europa.ec.empl.esco.openapi.goldenmaster.comparison;

import java.util.List;

/**
 * Result of comparing an actual response against a baseline.
 *
 * @param match       {@code true} if no differences were found
 * @param differences list of human-readable difference descriptions
 */
public record ComparisonResult(boolean match, List<String> differences) {

    public static ComparisonResult ok() {
        return new ComparisonResult(true, List.of());
    }

    public static ComparisonResult mismatch(List<String> differences) {
        return new ComparisonResult(false, differences);
    }
}

