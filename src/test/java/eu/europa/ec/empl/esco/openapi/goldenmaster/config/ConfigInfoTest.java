package eu.europa.ec.empl.esco.openapi.goldenmaster.config;

import eu.europa.ec.empl.esco.openapi.goldenmaster.assertion.GoldenMasterAssertions;
import eu.europa.ec.empl.esco.openapi.goldenmaster.client.ApiResponse;
import eu.europa.ec.empl.esco.openapi.goldenmaster.client.EscoClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Golden master tests for the {@code /config-info/*} endpoints.
 * <p>
 * Covers: getEscoPrefixes, getEscoDefaultVersion, getAvailableLanguages.
 */
@DisplayName("Config Info endpoints")
class ConfigInfoTest {

    private static final String GROUP = ConfigInfoTest.class.getPackageName()
            .substring(ConfigInfoTest.class.getPackageName().lastIndexOf('.') + 1);
    private static final String CLASS = ConfigInfoTest.class.getSimpleName();

    private final EscoClient client = EscoClient.instance();

    @Test
    @DisplayName("GET /config-info/prefixes → 200")
    void prefixes() {
        ApiResponse response = client.get("/config-info/prefixes");
        GoldenMasterAssertions.assertGoldenMaster(GROUP, CLASS, "prefixes", response, 200);
    }

    @Test
    @DisplayName("GET /config-info/default-version → 200")
    void defaultVersion() {
        ApiResponse response = client.get("/config-info/default-version");
        GoldenMasterAssertions.assertGoldenMaster(GROUP, CLASS, "default_version", response, 200);
    }

    @Test
    @DisplayName("GET /config-info/available-languages → 200")
    void availableLanguages() {
        ApiResponse response = client.get("/config-info/available-languages");
        GoldenMasterAssertions.assertGoldenMaster(GROUP, CLASS, "available_languages", response, 200);
    }
}

