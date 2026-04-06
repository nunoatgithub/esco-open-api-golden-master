package eu.europa.ec.empl.esco.openapi.goldenmaster.history;

import eu.europa.ec.empl.esco.openapi.goldenmaster.assertion.GoldenMasterAssertions;
import eu.europa.ec.empl.esco.openapi.goldenmaster.client.EscoClient;
import eu.europa.ec.empl.esco.openapi.goldenmaster.testdata.KnownUris;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Golden master tests for {@code /conversion} endpoints across skill, occupation, and concept.
 */
@DisplayName("Conversion")
class ConversionTest {

    private static final String GROUP = ConversionTest.class.getPackageName()
            .substring(ConversionTest.class.getPackageName().lastIndexOf('.') + 1);
    private static final String CLASS = ConversionTest.class.getSimpleName();
    private final EscoClient client = EscoClient.instance();

    @Test void skillConversion() {
        assertConversion("skill_conversion", KnownUris.SKILL_PATH + "/conversion?language=en");
    }

    @Test void occupationConversion() {
        assertConversion("occupation_conversion", KnownUris.OCCUPATION_PATH + "/conversion?language=en");
    }

    @Test void conceptConversion() {
        assertConversion("concept_conversion", KnownUris.CONCEPT_PATH + "/conversion?language=en");
    }

    private void assertConversion(String testName, String path) {
        var response = client.get(path);
        GoldenMasterAssertions.assertGoldenMaster(GROUP, CLASS, testName, response, 200);
    }
}
