package eu.europa.ec.empl.esco.openapi.goldenmaster.hierarchy;

import eu.europa.ec.empl.esco.openapi.goldenmaster.assertion.GoldenMasterAssertions;
import eu.europa.ec.empl.esco.openapi.goldenmaster.client.EscoClient;
import eu.europa.ec.empl.esco.openapi.goldenmaster.testdata.KnownUris;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Golden master tests for per-resource {@code /hierarchy} endpoints.
 */
@DisplayName("Resource hierarchy")
class ResourceHierarchyTest {

    private static final String GROUP = ResourceHierarchyTest.class.getPackageName()
            .substring(ResourceHierarchyTest.class.getPackageName().lastIndexOf('.') + 1);
    private static final String CLASS = ResourceHierarchyTest.class.getSimpleName();
    private final EscoClient client = EscoClient.instance();

    @Test void skillHierarchy() {
        assertHierarchy("skill_hierarchy", KnownUris.SKILL_PATH + "/hierarchy?language=en");
    }

    @Test void occupationHierarchy() {
        assertHierarchy("occupation_hierarchy", KnownUris.OCCUPATION_PATH + "/hierarchy?language=en");
    }

    @Test void conceptHierarchy() {
        assertHierarchy("concept_hierarchy", KnownUris.CONCEPT_PATH + "/hierarchy?language=en");
    }

    private void assertHierarchy(String testName, String path) {
        var response = client.get(path);
        GoldenMasterAssertions.assertGoldenMaster(GROUP, CLASS, testName, response, 200);
    }
}
