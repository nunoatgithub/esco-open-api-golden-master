package eu.europa.ec.empl.esco.openapi.goldenmaster.list;

import eu.europa.ec.empl.esco.openapi.goldenmaster.assertion.GoldenMasterAssertions;
import eu.europa.ec.empl.esco.openapi.goldenmaster.client.EscoClient;
import eu.europa.ec.empl.esco.openapi.goldenmaster.config.TestConfig;
import eu.europa.ec.empl.esco.openapi.goldenmaster.testdata.KnownUris;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Golden master tests for list-by-URIs endpoints:
 * {@code /skill-list}, {@code /occupation-list}, {@code /concept-list}, {@code /concept-scheme-list}.
 */
@DisplayName("List by URIs")
class ListByUrisTest {

    private static final String GROUP = ListByUrisTest.class.getPackageName()
            .substring(ListByUrisTest.class.getPackageName().lastIndexOf('.') + 1);
    private static final String CLASS = ListByUrisTest.class.getSimpleName();
    private final EscoClient client = EscoClient.instance();

    @Test void skillListByUris() {
        String uris = urisParam(KnownUris.SKILL_URI, KnownUris.SKILL_URI_2);
        assertList("skill_list_by_uris", "/skill-list?" + uris + "&language=en");
    }

    @Test void occupationListByUris() {
        String uris = urisParam(KnownUris.OCCUPATION_URI, KnownUris.OCCUPATION_URI_2);
        assertList("occupation_list_by_uris", "/occupation-list?" + uris + "&language=en");
    }

    @Test void conceptListByUris() {
        String uris = urisParam(KnownUris.CONCEPT_URI, KnownUris.CONCEPT_URI_2);
        assertList("concept_list_by_uris", "/concept-list?" + uris + "&language=en");
    }

    @Test void conceptSchemeList() {
        String uris = urisParam(KnownUris.CONCEPT_SCHEME_URI, KnownUris.CONCEPT_SCHEME_URI_2);
        assertList("concept_scheme_list", "/concept-scheme-list?" + uris + "&language=en");
    }

    @Test void skillListPinnedVersion() {
        String uris = urisParam(KnownUris.SKILL_URI, KnownUris.SKILL_URI_2);
        assertList("skill_list_pinned_version",
                "/skill-list?" + uris + "&language=en&selectedVersion=" + TestConfig.instance().datasetVersion());
    }

    private void assertList(String testName, String path) {
        var response = client.get(path);
        GoldenMasterAssertions.assertGoldenMaster(GROUP, CLASS, testName, response, 200);
    }

    private static String urisParam(String uri1, String uri2) {
        return "uris=" + encode(uri1) + "&uris=" + encode(uri2);
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
