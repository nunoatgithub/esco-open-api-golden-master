package eu.europa.ec.empl.esco.openapi.goldenmaster.client;

/**
 * Immutable container for an HTTP response from the ESCO API.
 *
 * @param uri        the request URI
 * @param status     HTTP status code
 * @param body       response body as a string
 * @param durationMs time taken to receive the response, in milliseconds
 */
public record ApiResponse(String uri, int status, String body, long durationMs) {
}
