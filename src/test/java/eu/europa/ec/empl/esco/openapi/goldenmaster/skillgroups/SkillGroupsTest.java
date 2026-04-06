package eu.europa.ec.empl.esco.openapi.goldenmaster.skillgroups;

import eu.europa.ec.empl.esco.openapi.goldenmaster.assertion.GoldenMasterAssertions;
import eu.europa.ec.empl.esco.openapi.goldenmaster.client.ApiResponse;
import eu.europa.ec.empl.esco.openapi.goldenmaster.client.EscoClient;
import eu.europa.ec.empl.esco.openapi.goldenmaster.testdata.KnownUris;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Golden master test for {@code GET /api/skill-groups}.
 */
@DisplayName("Skill Groups")
class SkillGroupsTest {

    private static final String GROUP = SkillGroupsTest.class.getPackageName()
            .substring(SkillGroupsTest.class.getPackageName().lastIndexOf('.') + 1);
    private static final String CLASS = SkillGroupsTest.class.getSimpleName();
    private final EscoClient client = EscoClient.instance();

    @Test
    @DisplayName("GET /api/skill-groups → 200")
    void skillGroups() {
        String occUri = URLEncoder.encode(KnownUris.OCCUPATION_URI, StandardCharsets.UTF_8);
        ApiResponse response = client.get(
                "/api/skill-groups?occupationUri=" + occUri + "&language=en&version=v1.2.0");
        GoldenMasterAssertions.assertGoldenMaster(GROUP, CLASS, "skill_groups", response, 200);
    }
}

