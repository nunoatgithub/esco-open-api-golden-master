package eu.europa.ec.empl.esco.openapi.goldenmaster.history;

import eu.europa.ec.empl.esco.openapi.goldenmaster.assertion.GoldenMasterAssertions;
import eu.europa.ec.empl.esco.openapi.goldenmaster.client.EscoClient;
import eu.europa.ec.empl.esco.openapi.goldenmaster.testdata.KnownUris;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Golden master tests for {@code /history} endpoints across skill, occupation, and concept.
 * <p>
 * Covers: default history and fullHistory=true.
 */
@DisplayName("History")
class HistoryTest {

    private static final String GROUP = HistoryTest.class.getPackageName()
            .substring(HistoryTest.class.getPackageName().lastIndexOf('.') + 1);
    private static final String CLASS = HistoryTest.class.getSimpleName();
    private final EscoClient client = EscoClient.instance();

    @Test void skillHistory() {
        assertHistory("skill_history", KnownUris.SKILL_PATH + "/history?language=en");
    }

    @Test void skillHistoryFull() {
        assertHistory("skill_history_full", KnownUris.SKILL_PATH + "/history?fullHistory=true&language=en");
    }

    @Test void occupationHistory() {
        assertHistory("occupation_history", KnownUris.OCCUPATION_PATH + "/history?language=en");
    }

    @Test void occupationHistoryFull() {
        assertHistory("occupation_history_full", KnownUris.OCCUPATION_PATH + "/history?fullHistory=true&language=en");
    }

    @Test void conceptHistory() {
        assertHistory("concept_history", KnownUris.CONCEPT_PATH + "/history?language=en");
    }

    @Test void conceptHistoryFull() {
        assertHistory("concept_history_full", KnownUris.CONCEPT_PATH + "/history?fullHistory=true&language=en");
    }

    private void assertHistory(String testName, String path) {
        var response = client.get(path);
        GoldenMasterAssertions.assertGoldenMaster(GROUP, CLASS, testName, response, 200);
    }
}
