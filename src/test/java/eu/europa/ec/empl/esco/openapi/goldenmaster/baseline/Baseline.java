package eu.europa.ec.empl.esco.openapi.goldenmaster.baseline;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.Instant;

/**
 * Immutable representation of a captured baseline response.
 *
 * @param capturedAt      when the baseline was captured
 * @param capturedBaseUrl the API base URL used when this baseline was captured
 * @param responseTimeMs  response time in milliseconds at capture time
 * @param httpStatus      HTTP status code
 * @param body            response body (JsonNode for JSON, TextNode for plain text/RDF)
 */
public record Baseline(Instant capturedAt, String capturedBaseUrl, long responseTimeMs, int httpStatus, JsonNode body) {
}

