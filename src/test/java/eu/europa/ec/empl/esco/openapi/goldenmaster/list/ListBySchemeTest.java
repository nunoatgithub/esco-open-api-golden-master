package eu.europa.ec.empl.esco.openapi.goldenmaster.list;

import eu.europa.ec.empl.esco.openapi.goldenmaster.assertion.GoldenMasterAssertions;
import eu.europa.ec.empl.esco.openapi.goldenmaster.client.EscoClient;
import eu.europa.ec.empl.esco.openapi.goldenmaster.testdata.KnownUris;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Golden master tests for list-by-scheme endpoints:
 * {@code /skill-list/by-scheme}, {@code /occupation-list/by-scheme}, {@code /concept-list/by-scheme}.
 * <p>
 * Covers: pagination (limit/offset), viewObsolete.
 */
@DisplayName("List by scheme")
class ListBySchemeTest {

    private static final String GROUP = ListBySchemeTest.class.getPackageName()
            .substring(ListBySchemeTest.class.getPackageName().lastIndexOf('.') + 1);
    private static final String CLASS = ListBySchemeTest.class.getSimpleName();
    private final EscoClient client = EscoClient.instance();

    // ── Skill ──

    @Test void skillByScheme() {
        assertByScheme("skill_by_scheme",
                "/skill-list/by-scheme?inScheme=" + encode(KnownUris.SKILLS_SCHEME_URI) + "&limit=5&language=en");
    }

    @Test void skillBySchemePage2() {
        assertByScheme("skill_by_scheme_page2",
                "/skill-list/by-scheme?inScheme=" + encode(KnownUris.SKILLS_SCHEME_URI) + "&limit=5&offset=5&language=en");
    }

    @Test void skillBySchemeObsolete() {
        assertByScheme("skill_by_scheme_obsolete",
                "/skill-list/by-scheme?inScheme=" + encode(KnownUris.SKILLS_SCHEME_URI) + "&viewObsolete=true&language=en");
    }

    // ── Occupation ──

    @Test void occupationByScheme() {
        assertByScheme("occupation_by_scheme",
                "/occupation-list/by-scheme?inScheme=" + encode(KnownUris.OCCUPATIONS_SCHEME_URI) + "&limit=5&language=en");
    }

    @Test void occupationBySchemePage2() {
        assertByScheme("occupation_by_scheme_page2",
                "/occupation-list/by-scheme?inScheme=" + encode(KnownUris.OCCUPATIONS_SCHEME_URI) + "&limit=5&offset=5&language=en");
    }

    @Test void occupationBySchemeObsolete() {
        assertByScheme("occupation_by_scheme_obsolete",
                "/occupation-list/by-scheme?inScheme=" + encode(KnownUris.OCCUPATIONS_SCHEME_URI) + "&viewObsolete=true&language=en");
    }

    // ── Concept ──

    @Test void conceptByScheme() {
        assertByScheme("concept_by_scheme",
                "/concept-list/by-scheme?inScheme=" + encode(KnownUris.ISCO_SCHEME_URI) + "&limit=5&language=en");
    }

    @Test void conceptBySchemePage2() {
        assertByScheme("concept_by_scheme_page2",
                "/concept-list/by-scheme?inScheme=" + encode(KnownUris.ISCO_SCHEME_URI) + "&limit=5&offset=5&language=en");
    }

    @Test void conceptBySchemeObsolete() {
        assertByScheme("concept_by_scheme_obsolete",
                "/concept-list/by-scheme?inScheme=" + encode(KnownUris.ISCO_SCHEME_URI) + "&viewObsolete=true&language=en");
    }

    // ── Helper ──

    private void assertByScheme(String testName, String path) {
        var response = client.get(path);
        GoldenMasterAssertions.assertGoldenMaster(GROUP, CLASS, testName, response, 200);
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
