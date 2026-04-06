package eu.europa.ec.empl.esco.openapi.goldenmaster.resource;

import eu.europa.ec.empl.esco.openapi.goldenmaster.assertion.GoldenMasterAssertions;
import eu.europa.ec.empl.esco.openapi.goldenmaster.client.EscoClient;
import eu.europa.ec.empl.esco.openapi.goldenmaster.config.TestConfig;
import eu.europa.ec.empl.esco.openapi.goldenmaster.testdata.KnownUris;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Golden master tests for {@code GET /skill/{prefix}:{id}}.
 * <p>
 * Covers: language variants, pinned version.
 */
@DisplayName("Skill — single resource retrieval")
class SkillGetTest {

    private static final String GROUP = SkillGetTest.class.getPackageName()
            .substring(SkillGetTest.class.getPackageName().lastIndexOf('.') + 1);
    private static final String CLASS = SkillGetTest.class.getSimpleName();
    private final EscoClient client = EscoClient.instance();

    @Test void skillEn() {
        assertSkill("skill_en", "en");
    }

    @Test void skillPt() {
        assertSkill("skill_pt", "pt");
    }

    @Test void skillPinnedVersion() {
        var response = client.get(KnownUris.SKILL_PATH + "?language=en&selectedVersion=" + TestConfig.instance().datasetVersion());
        GoldenMasterAssertions.assertGoldenMaster(GROUP, CLASS, "skill_pinned_version", response, 200);
    }

    private void assertSkill(String testName, String lang) {
        var response = client.get(KnownUris.SKILL_PATH + "?language=" + lang);
        GoldenMasterAssertions.assertGoldenMaster(GROUP, CLASS, testName, response, 200);
    }
}
