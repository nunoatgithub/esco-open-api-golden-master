package eu.europa.ec.empl.esco.openapi.goldenmaster.search;

import eu.europa.ec.empl.esco.openapi.goldenmaster.assertion.GoldenMasterAssertions;
import eu.europa.ec.empl.esco.openapi.goldenmaster.client.EscoClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Golden master tests for {@code POST /api/search}.
 * <p>
 * Covers: conceptTypes, statuses, codes, reuseLevels, labels, text, page/pageSize, empty body.
 */
@DisplayName("Search — POST")
class SearchPostTest {

    private static final String GROUP = SearchPostTest.class.getPackageName()
            .substring(SearchPostTest.class.getPackageName().lastIndexOf('.') + 1);
    private static final String CLASS = SearchPostTest.class.getSimpleName();
    private final EscoClient client = EscoClient.instance();

    @Test void postOccupations() {
        assertSearchPost("post_occupations", """
                {"conceptTypes":["occupation"],"text":"firefighter"}""");
    }

    @Test void postSkills() {
        assertSearchPost("post_skills", """
                {"conceptTypes":["skill"],"text":"programming"}""");
    }

    @Test void postWithCodes() {
        assertSearchPost("post_with_codes", """
                {"codes":["2320"],"conceptTypes":["occupation"]}""");
    }

    @Test void postWithStatuses() {
        assertSearchPost("post_with_statuses", """
                {"statuses":["released"],"conceptTypes":["skill"],"text":"data"}""");
    }

    @Test void postWithReuseLevels() {
        assertSearchPost("post_with_reuse_levels", """
                {"reuseLevels":["http://data.europa.eu/esco/skill-reuse-level/cross-sector"],"conceptTypes":["skill"]}""");
    }

    @Test void postWithLabels() {
        assertSearchPost("post_with_labels", """
                {"labels":["http://data.europa.eu/esco/concept-scheme/skills"],"conceptTypes":["skill"]}""");
    }

    @Test void postWithPageSize() {
        assertSearchPost("post_with_page_size", """
                {"conceptTypes":["occupation"],"text":"engineer","page":2,"pageSize":3}""");
    }

    @Test void postEmptyBody() {
        assertSearchPost("post_empty_body", "{}");
    }

    private void assertSearchPost(String testName, String jsonBody) {
        var response = client.post("/api/search", jsonBody);
        GoldenMasterAssertions.assertGoldenMaster(GROUP, CLASS, testName, response);
    }
}
