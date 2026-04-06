package eu.europa.ec.empl.esco.openapi.goldenmaster.resource;

import eu.europa.ec.empl.esco.openapi.goldenmaster.assertion.GoldenMasterAssertions;
import eu.europa.ec.empl.esco.openapi.goldenmaster.client.EscoClient;
import eu.europa.ec.empl.esco.openapi.goldenmaster.config.TestConfig;
import eu.europa.ec.empl.esco.openapi.goldenmaster.testdata.KnownUris;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Golden master tests for {@code GET /concept-scheme/{prefix}:{id}}.
 */
@DisplayName("Concept Scheme — single resource retrieval")
class ConceptSchemeGetTest {

    private static final String GROUP = ConceptSchemeGetTest.class.getPackageName()
            .substring(ConceptSchemeGetTest.class.getPackageName().lastIndexOf('.') + 1);
    private static final String CLASS = ConceptSchemeGetTest.class.getSimpleName();
    private final EscoClient client = EscoClient.instance();

    @Test void conceptSchemeEn() {
        assertConceptScheme("concept_scheme_en", "en");
    }

    @Test void conceptSchemePt() {
        assertConceptScheme("concept_scheme_pt", "pt");
    }

    @Test void conceptSchemePinnedVersion() {
        var response = client.get(KnownUris.CONCEPT_SCHEME_PATH + "?language=en&selectedVersion=" + TestConfig.instance().datasetVersion());
        GoldenMasterAssertions.assertGoldenMaster(GROUP, CLASS, "concept_scheme_pinned_version", response, 200);
    }

    private void assertConceptScheme(String testName, String lang) {
        var response = client.get(KnownUris.CONCEPT_SCHEME_PATH + "?language=" + lang);
        GoldenMasterAssertions.assertGoldenMaster(GROUP, CLASS, testName, response, 200);
    }
}
