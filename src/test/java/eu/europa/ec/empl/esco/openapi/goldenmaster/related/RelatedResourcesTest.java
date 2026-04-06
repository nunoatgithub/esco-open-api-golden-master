package eu.europa.ec.empl.esco.openapi.goldenmaster.related;

import eu.europa.ec.empl.esco.openapi.goldenmaster.assertion.GoldenMasterAssertions;
import eu.europa.ec.empl.esco.openapi.goldenmaster.client.EscoClient;
import eu.europa.ec.empl.esco.openapi.goldenmaster.testdata.KnownUris;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Golden master tests for {@code /related} and {@code /related-hierarchy} endpoints
 * across skill, occupation, and concept resource types.
 * <p>
 * Covers: relation param, inScheme filtering, limit/offset pagination.
 */
@DisplayName("Related resources")
class RelatedResourcesTest {

    private static final String GROUP = RelatedResourcesTest.class.getPackageName()
            .substring(RelatedResourcesTest.class.getPackageName().lastIndexOf('.') + 1);
    private static final String CLASS = RelatedResourcesTest.class.getSimpleName();
    private final EscoClient client = EscoClient.instance();

    // ── Skill ──

    @Test void skillRelated() {
        assertRelated("skill_related",
                KnownUris.SKILL_PATH + "/related?relation=" + KnownUris.RELATION_ESSENTIAL_SKILL_FOR_OCCUPATION + "&limit=5&language=en");
    }

    @Test void skillRelatedWithScheme() {
        assertRelated("skill_related_with_scheme",
                KnownUris.SKILL_PATH + "/related?relation=" + KnownUris.RELATION_ESSENTIAL_SKILL_FOR_OCCUPATION
                        + "&inScheme=" + KnownUris.SKILLS_SCHEME_URI + "&language=en");
    }

    @Test void skillRelatedPage2() {
        assertRelated("skill_related_page2",
                KnownUris.SKILL_PATH + "/related?relation=" + KnownUris.RELATION_ESSENTIAL_SKILL_FOR_OCCUPATION
                        + "&limit=3&offset=3&language=en");
    }

    @Test void skillRelatedHierarchy() {
        assertRelated("skill_related_hierarchy",
                KnownUris.SKILL_PATH + "/related-hierarchy?relation=" + KnownUris.RELATION_ESSENTIAL_SKILL_FOR_OCCUPATION + "&language=en");
    }

    // ── Occupation ──

    @Test void occupationRelated() {
        assertRelated("occupation_related",
                KnownUris.OCCUPATION_PATH + "/related?relation=" + KnownUris.RELATION_ESSENTIAL_SKILL + "&limit=5&language=en");
    }

    @Test void occupationRelatedWithScheme() {
        assertRelated("occupation_related_with_scheme",
                KnownUris.OCCUPATION_PATH + "/related?relation=" + KnownUris.RELATION_ESSENTIAL_SKILL
                        + "&inScheme=" + KnownUris.SKILLS_SCHEME_URI + "&language=en");
    }

    @Test void occupationRelatedPage2() {
        assertRelated("occupation_related_page2",
                KnownUris.OCCUPATION_PATH + "/related?relation=" + KnownUris.RELATION_ESSENTIAL_SKILL
                        + "&limit=3&offset=3&language=en");
    }

    @Test void occupationRelatedHierarchy() {
        assertRelated("occupation_related_hierarchy",
                KnownUris.OCCUPATION_PATH + "/related-hierarchy?relation=" + KnownUris.RELATION_ESSENTIAL_SKILL + "&language=en");
    }

    // ── Concept ──

    @Test void conceptRelated() {
        assertRelated("concept_related",
                KnownUris.CONCEPT_PATH + "/related?relation=" + KnownUris.RELATION_NARROWER_CONCEPT + "&limit=5&language=en");
    }

    @Test void conceptRelatedWithScheme() {
        assertRelated("concept_related_with_scheme",
                KnownUris.CONCEPT_PATH + "/related?relation=" + KnownUris.RELATION_NARROWER_CONCEPT
                        + "&inScheme=" + KnownUris.ISCO_SCHEME_URI + "&language=en");
    }

    @Test void conceptRelatedPage2() {
        assertRelated("concept_related_page2",
                KnownUris.CONCEPT_PATH + "/related?relation=" + KnownUris.RELATION_NARROWER_CONCEPT
                        + "&limit=3&offset=3&language=en");
    }

    @Test void conceptRelatedHierarchy() {
        assertRelated("concept_related_hierarchy",
                KnownUris.CONCEPT_PATH + "/related-hierarchy?relation=" + KnownUris.RELATION_NARROWER_CONCEPT + "&language=en");
    }

    // ── Helper ──

    private void assertRelated(String testName, String path) {
        var response = client.get(path);
        GoldenMasterAssertions.assertGoldenMaster(GROUP, CLASS, testName, response, 200);
    }
}
