package eu.europa.ec.empl.esco.openapi.goldenmaster.resource;

import eu.europa.ec.empl.esco.openapi.goldenmaster.assertion.GoldenMasterAssertions;
import eu.europa.ec.empl.esco.openapi.goldenmaster.client.EscoClient;
import eu.europa.ec.empl.esco.openapi.goldenmaster.config.TestConfig;
import eu.europa.ec.empl.esco.openapi.goldenmaster.testdata.KnownUris;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Golden master tests for {@code GET /concept/{prefix}:{id}}.
 */
@DisplayName("Concept — single resource retrieval")
class ConceptGetTest {

    private static final String GROUP = ConceptGetTest.class.getPackageName()
            .substring(ConceptGetTest.class.getPackageName().lastIndexOf('.') + 1);
    private static final String CLASS = ConceptGetTest.class.getSimpleName();
    private final EscoClient client = EscoClient.instance();

    @Test void conceptEn() {
        assertConcept("concept_en", "en");
    }

    @Test void conceptPt() {
        assertConcept("concept_pt", "pt");
    }

    @Test void conceptPinnedVersion() {
        var response = client.get(KnownUris.CONCEPT_PATH + "?language=en&selectedVersion=" + TestConfig.instance().datasetVersion());
        GoldenMasterAssertions.assertGoldenMaster(GROUP, CLASS, "concept_pinned_version", response, 200);
    }

    private void assertConcept(String testName, String lang) {
        var response = client.get(KnownUris.CONCEPT_PATH + "?language=" + lang);
        GoldenMasterAssertions.assertGoldenMaster(GROUP, CLASS, testName, response, 200);
    }
}
