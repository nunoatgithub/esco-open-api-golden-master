package eu.europa.ec.empl.esco.openapi.goldenmaster.contentneg;

import eu.europa.ec.empl.esco.openapi.goldenmaster.assertion.GoldenMasterAssertions;
import eu.europa.ec.empl.esco.openapi.goldenmaster.client.EscoClient;
import eu.europa.ec.empl.esco.openapi.goldenmaster.testdata.KnownUris;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

/**
 * Golden master tests for content negotiation (RDF/XML + Turtle) across all 4 resource types.
 */
@DisplayName("Content Negotiation — RDF/XML and Turtle")
class ContentNegotiationTest {

    private static final String GROUP = ContentNegotiationTest.class.getPackageName()
            .substring(ContentNegotiationTest.class.getPackageName().lastIndexOf('.') + 1);
    private static final String CLASS = ContentNegotiationTest.class.getSimpleName();
    private final EscoClient client = EscoClient.instance();

    @Test void skillRdfXml() {
        assertContentNeg("skill_rdf_xml", KnownUris.SKILL_PATH + "?language=en", "application/rdf+xml");
    }

    @Test void skillTurtle() {
        assertContentNeg("skill_turtle", KnownUris.SKILL_PATH + "?language=en", "text/turtle");
    }

    @Test void occupationRdfXml() {
        assertContentNeg("occupation_rdf_xml", KnownUris.OCCUPATION_PATH + "?language=en", "application/rdf+xml");
    }

    @Test void occupationTurtle() {
        assertContentNeg("occupation_turtle", KnownUris.OCCUPATION_PATH + "?language=en", "text/turtle");
    }

    @Test void conceptRdfXml() {
        assertContentNeg("concept_rdf_xml", KnownUris.CONCEPT_PATH + "?language=en", "application/rdf+xml");
    }

    @Test void conceptTurtle() {
        assertContentNeg("concept_turtle", KnownUris.CONCEPT_PATH + "?language=en", "text/turtle");
    }

    @Test void conceptSchemeRdfXml() {
        assertContentNeg("concept_scheme_rdf_xml", KnownUris.CONCEPT_SCHEME_PATH + "?language=en", "application/rdf+xml");
    }

    @Test void conceptSchemeTurtle() {
        assertContentNeg("concept_scheme_turtle", KnownUris.CONCEPT_SCHEME_PATH + "?language=en", "text/turtle");
    }

    private void assertContentNeg(String testName, String path, String acceptHeader) {
        var response = client.get(path, Map.of("Accept", acceptHeader));
        GoldenMasterAssertions.assertGoldenMaster(GROUP, CLASS, testName, response, 200);
    }
}
