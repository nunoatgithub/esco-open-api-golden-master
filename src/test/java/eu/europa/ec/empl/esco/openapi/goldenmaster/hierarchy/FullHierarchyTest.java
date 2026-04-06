package eu.europa.ec.empl.esco.openapi.goldenmaster.hierarchy;

import eu.europa.ec.empl.esco.openapi.goldenmaster.assertion.GoldenMasterAssertions;
import eu.europa.ec.empl.esco.openapi.goldenmaster.client.EscoClient;
import eu.europa.ec.empl.esco.openapi.goldenmaster.config.TestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Golden master tests for {@code /skill-list/full-hierarchy} and
 * {@code /occupation-list/full-hierarchy} endpoints, including viewObsolete and pinned version.
 */
@DisplayName("Full hierarchy")
class FullHierarchyTest {

    private static final String GROUP = FullHierarchyTest.class.getPackageName()
            .substring(FullHierarchyTest.class.getPackageName().lastIndexOf('.') + 1);
    private static final String CLASS = FullHierarchyTest.class.getSimpleName();
    private final EscoClient client = EscoClient.instance();

    @Test void skillFullHierarchy() {
        assertFullHierarchy("skill_full_hierarchy", "/skill-list/full-hierarchy?language=en");
    }

    @Test void skillFullHierarchyObsolete() {
        assertFullHierarchy("skill_full_hierarchy_obsolete", "/skill-list/full-hierarchy?language=en&viewObsolete=true");
    }

    @Test void occupationFullHierarchy() {
        assertFullHierarchy("occupation_full_hierarchy", "/occupation-list/full-hierarchy?language=en");
    }

    @Test void occupationFullHierarchyObsolete() {
        assertFullHierarchy("occupation_full_hierarchy_obsolete", "/occupation-list/full-hierarchy?language=en&viewObsolete=true");
    }

    @Test void skillFullHierarchyPinned() {
        assertFullHierarchy("skill_full_hierarchy_pinned",
                "/skill-list/full-hierarchy?language=en&selectedVersion=" + TestConfig.instance().datasetVersion());
    }

    private void assertFullHierarchy(String testName, String path) {
        var response = client.get(path);
        GoldenMasterAssertions.assertGoldenMaster(GROUP, CLASS, testName, response, 200);
    }
}
