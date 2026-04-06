package eu.europa.ec.empl.esco.openapi.goldenmaster.resource;

import eu.europa.ec.empl.esco.openapi.goldenmaster.assertion.GoldenMasterAssertions;
import eu.europa.ec.empl.esco.openapi.goldenmaster.client.EscoClient;
import eu.europa.ec.empl.esco.openapi.goldenmaster.config.TestConfig;
import eu.europa.ec.empl.esco.openapi.goldenmaster.testdata.KnownUris;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Golden master tests for {@code GET /occupation/{prefix}:{id}}.
 */
@DisplayName("Occupation — single resource retrieval")
class OccupationGetTest {

    private static final String GROUP = OccupationGetTest.class.getPackageName()
            .substring(OccupationGetTest.class.getPackageName().lastIndexOf('.') + 1);
    private static final String CLASS = OccupationGetTest.class.getSimpleName();
    private final EscoClient client = EscoClient.instance();

    @Test void occupationEn() {
        assertOccupation("occupation_en", "en");
    }

    @Test void occupationPt() {
        assertOccupation("occupation_pt", "pt");
    }

    @Test void occupationPinnedVersion() {
        var response = client.get(KnownUris.OCCUPATION_PATH + "?language=en&selectedVersion=" + TestConfig.instance().datasetVersion());
        GoldenMasterAssertions.assertGoldenMaster(GROUP, CLASS, "occupation_pinned_version", response, 200);
    }

    private void assertOccupation(String testName, String lang) {
        var response = client.get(KnownUris.OCCUPATION_PATH + "?language=" + lang);
        GoldenMasterAssertions.assertGoldenMaster(GROUP, CLASS, testName, response, 200);
    }
}
