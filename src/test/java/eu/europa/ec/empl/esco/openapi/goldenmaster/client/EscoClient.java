package eu.europa.ec.empl.esco.openapi.goldenmaster.client;

import eu.europa.ec.empl.esco.openapi.goldenmaster.config.TestConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

/**
 * Lightweight HTTP client for the ESCO Open API.
 * Applies throttle delay between requests and measures response time.
 */
public final class EscoClient {

    private static final Logger LOG = LoggerFactory.getLogger(EscoClient.class);
    private static final EscoClient INSTANCE = new EscoClient();

    private final HttpClient httpClient;
    private final String baseUrl;
    private final long throttleMs;

    private EscoClient() {
        TestConfig cfg = TestConfig.instance();
        this.baseUrl = cfg.baseUrl();
        this.throttleMs = cfg.throttleMs();
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    public static EscoClient instance() {
        return INSTANCE;
    }

    /**
     * Performs a GET request against the ESCO API.
     *
     * @param path    relative path (e.g. {@code /skill/esco:abc123})
     * @param headers additional headers (e.g. {@code Accept-Language → en})
     * @return the API response
     */
    public ApiResponse get(String path, Map<String, String> headers) {
        throttle();
        URI uri = URI.create(baseUrl + path);
        LOG.info("GET {}", uri);

        HttpRequest.Builder builder = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(60))
                .GET();
        headers.forEach(builder::header);

        return execute(builder.build());
    }

    /**
     * Performs a GET request with no extra headers.
     */
    public ApiResponse get(String path) {
        return get(path, Map.of());
    }

    /**
     * Performs a POST request with a JSON body.
     *
     * @param path     relative path (e.g. {@code /api/search})
     * @param jsonBody JSON string to send as the request body
     * @param headers  additional headers
     * @return the API response
     */
    public ApiResponse post(String path, String jsonBody, Map<String, String> headers) {
        throttle();
        URI uri = URI.create(baseUrl + path);
        LOG.info("POST {} body={}", uri, jsonBody);

        HttpRequest.Builder builder = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(60))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody));
        headers.forEach(builder::header);

        return execute(builder.build());
    }

    /**
     * Performs a POST request with no extra headers.
     */
    public ApiResponse post(String path, String jsonBody) {
        return post(path, jsonBody, Map.of());
    }

    private ApiResponse execute(HttpRequest request) {
        try {
            long start = System.currentTimeMillis();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            long durationMs = System.currentTimeMillis() - start;

            LOG.info("  → {} ({} ms)", response.statusCode(), durationMs);
            LOG.debug("  → body: {}", response.body());
            return new ApiResponse(request.uri().toString(), response.statusCode(), response.body(), durationMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Request interrupted: " + request.uri(), e);
        } catch (Exception e) {
            throw new RuntimeException("Request failed: " + request.uri(), e);
        }
    }

    private void throttle() {
        if (throttleMs > 0) {
            try {
                Thread.sleep(throttleMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
