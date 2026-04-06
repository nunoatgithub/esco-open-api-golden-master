package eu.europa.ec.empl.esco.openapi.goldenmaster.search;

import eu.europa.ec.empl.esco.openapi.goldenmaster.assertion.GoldenMasterAssertions;
import eu.europa.ec.empl.esco.openapi.goldenmaster.client.EscoClient;
import eu.europa.ec.empl.esco.openapi.goldenmaster.testdata.KnownUris;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Golden master tests for {@code GET /search}.
 * <p>
 * Covers: text, type, facets, hasLabel, hasLabelRole, inScheme, pagination, viewObsolete, empty result.
 */
@DisplayName("Search — GET")
class SearchGetTest {

    private static final String GROUP = SearchGetTest.class.getPackageName()
            .substring(SearchGetTest.class.getPackageName().lastIndexOf('.') + 1);
    private static final String CLASS = SearchGetTest.class.getSimpleName();
    private final EscoClient client = EscoClient.instance();

    @Test void getOccupations() {
        assertSearch("get_occupations", "/search?text=nurse&type=occupation&language=en&limit=5");
    }

    @Test void getSkills() {
        assertSearch("get_skills", "/search?text=python&type=skill&language=en");
    }

    @Test void getConcepts() {
        assertSearch("get_concepts", "/search?text=ICT&type=concept&language=en");
    }

    @Test void getWithFacets() {
        assertSearch("get_with_facets", "/search?text=data&facet=type&facet=inScheme&language=en");
    }

    @Test void getWithLabelRoleNeutral() {
        assertSearch("get_with_label_role_neutral",
                "/search?text=teacher&type=occupation&hasLabelRole=" + encode("http://data.europa.eu/esco/label-role/neutral") + "&language=en");
    }

    @Test void getWithLabelRoleFemale() {
        assertSearch("get_with_label_role_female",
                "/search?text=teacher&type=occupation&hasLabelRole=" + encode("http://data.europa.eu/esco/label-role/standard-female") + "&language=en");
    }

    @Test void getWithHasLabel() {
        assertSearch("get_with_hasLabel", "/search?text=programming&hasLabel=prefLabel&hasLabel=altLabel&language=en");
    }

    @Test void getWithInScheme() {
        assertSearch("get_with_inScheme",
                "/search?text=data&inScheme=" + encode(KnownUris.SKILLS_SCHEME_URI) + "&type=skill&language=en");
    }

    @Test void getPage2() {
        assertSearch("get_page2", "/search?text=data&type=skill&limit=5&offset=5&language=en");
    }

    @Test void getWithObsolete() {
        assertSearch("get_with_obsolete", "/search?text=data&type=skill&viewObsolete=true&language=en");
    }

    @Test void getEmptyResult() {
        assertSearch("get_empty_result", "/search?text=xyznonexistent12345&language=en");
    }

    private void assertSearch(String testName, String path) {
        var response = client.get(path);
        GoldenMasterAssertions.assertGoldenMaster(GROUP, CLASS, testName, response, 200);
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
