package eu.europa.ec.empl.esco.openapi.goldenmaster.error;

import eu.europa.ec.empl.esco.openapi.goldenmaster.assertion.GoldenMasterAssertions;
import eu.europa.ec.empl.esco.openapi.goldenmaster.client.EscoClient;
import eu.europa.ec.empl.esco.openapi.goldenmaster.testdata.KnownUris;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Golden master tests for error responses (404 and 400).
 */
@DisplayName("Error handling")
class ErrorHandlingTest {

    private static final String GROUP = ErrorHandlingTest.class.getPackageName()
            .substring(ErrorHandlingTest.class.getPackageName().lastIndexOf('.') + 1);
    private static final String BUGS_GROUP = "bugs";
    private static final String CLASS = ErrorHandlingTest.class.getSimpleName();
    private final EscoClient client = EscoClient.instance();

    // ── 404 Not Found ──

    @Test void skillNotFound() {
        assertNotFound("skill_not_found", KnownUris.NONEXISTENT_SKILL_PATH + "?language=en");
    }

    @Test void occupationNotFound() {
        assertNotFound("occupation_not_found", KnownUris.NONEXISTENT_OCCUPATION_PATH + "?language=en");
    }

    @Test void conceptNotFound() {
        assertNotFound("concept_not_found", KnownUris.NONEXISTENT_CONCEPT_PATH + "?language=en");
    }

    @Test void conceptSchemeNotFound() {
        assertNotFound("concept_scheme_not_found", KnownUris.NONEXISTENT_CONCEPT_SCHEME_PATH + "?language=en");
    }

    @Test void skillHierarchyNotFound() {
        assertNotFound("skill_hierarchy_not_found", KnownUris.NONEXISTENT_SKILL_PATH + "/hierarchy?language=en");
    }

    // ── Potential API bugs: spec lists 404 but API returns something else ──
    // These are baselined under baselines/bugs/ to track actual (incorrect) behavior.

    // Spec says 404, API returns 500 with exceptionId=NotFoundException
    @Test void occupationHistoryNotFound() {
        assertPotentialBug("occupation_history_not_found",
                KnownUris.NONEXISTENT_OCCUPATION_PATH + "/history?language=en");
    }

    // Spec says 404, API returns 200 with empty results (doesn't validate parent existence)
    @Test void conceptRelatedNotFound() {
        assertPotentialBug("concept_related_not_found",
                KnownUris.NONEXISTENT_CONCEPT_PATH + "/related?relation=narrowerConcept&language=en");
    }

    // Spec says 404, API returns 500 with exceptionId=NotFoundException
    @Test void skillConversionNotFound() {
        assertPotentialBug("skill_conversion_not_found",
                KnownUris.NONEXISTENT_SKILL_PATH + "/conversion?language=en");
    }

    // ── 400 Bad Request ──

    @Test void relatedMissingRelation() {
        assertBadRequest("related_missing_relation", KnownUris.SKILL_PATH + "/related?language=en");
    }

    @Test void relatedHierarchyMissingRelation() {
        assertBadRequest("related_hierarchy_missing_relation", KnownUris.OCCUPATION_PATH + "/related-hierarchy?language=en");
    }

    @Test void relatedInvalidRelation() {
        assertBadRequest("related_invalid_relation", KnownUris.OCCUPATION_PATH + "/related?relation=nonsense&language=en");
    }

    @Test void listMissingUris() {
        assertBadRequest("list_missing_uris", "/skill-list?language=en");
    }

    @Test void occupationListMissingUris() {
        assertBadRequest("occupation_list_missing_uris", "/occupation-list?language=en");
    }

    @Test void bySchemeMissingInScheme() {
        assertBadRequest("by_scheme_missing_inScheme", "/skill-list/by-scheme?language=en");
    }

    @Test void bySchemeInvalidLimit() {
        String skillsScheme = encode(KnownUris.SKILLS_SCHEME_URI);
        assertBadRequest("by_scheme_invalid_limit", "/skill-list/by-scheme?inScheme=" + skillsScheme + "&limit=-1&language=en");
    }

    @Test void bySchemeInvalidOffset() {
        String skillsScheme = encode(KnownUris.SKILLS_SCHEME_URI);
        assertBadRequest("by_scheme_invalid_offset", "/concept-list/by-scheme?inScheme=" + skillsScheme + "&offset=-1&language=en");
    }

    @Test void searchInvalidType() {
        assertBadRequest("search_invalid_type", "/search?type=invalid&language=en");
    }

    @Test void skillGroupsMissingParams() {
        assertBadRequest("skill_groups_missing_params", "/api/skill-groups");
    }

    // ── Helpers ──

    private void assertNotFound(String testName, String path) {
        var response = client.get(path);
        GoldenMasterAssertions.assertGoldenMaster(GROUP, CLASS, testName, response, 404);
    }

    private void assertBadRequest(String testName, String path) {
        var response = client.get(path);
        GoldenMasterAssertions.assertGoldenMaster(GROUP, CLASS, testName, response);
    }

    private void assertPotentialBug(String testName, String path) {
        var response = client.get(path);
        GoldenMasterAssertions.assertGoldenMaster(BUGS_GROUP, CLASS, testName, response);
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
