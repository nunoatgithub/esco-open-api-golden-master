package eu.europa.ec.empl.esco.openapi.goldenmaster.testdata;

/**
 * Known stable URIs for ESCO resources used across test cases.
 * <p>
 * These are well-known resources from the ESCO classification that are expected to remain
 * stable across API versions. All IDs are pinned to avoid flakiness.
 */
public final class KnownUris {

    private KnownUris() {
    }

    // ── Skills ──────────────────────────────────────────────────────────────

    /** "Python (computer programming)" — a real skill from ESCO search results */
    public static final String SKILL_PREFIX = "skill";
    public static final String SKILL_ID = "ccd0a1d9-afda-43d9-b901-96344886e14d";
    public static final String SKILL_PATH = "/skill/" + SKILL_PREFIX + ":" + SKILL_ID;
    public static final String SKILL_URI = "http://data.europa.eu/esco/skill/" + SKILL_ID;

    /** "use scripting programming" — a second real skill for list tests */
    public static final String SKILL_ID_2 = "5ef0c719-5bcb-49f8-b8eb-824388225333";
    public static final String SKILL_URI_2 = "http://data.europa.eu/esco/skill/" + SKILL_ID_2;

    // ── Occupations ─────────────────────────────────────────────────────────

    /** "specialist nurse" — a real occupation from ESCO search results */
    public static final String OCCUPATION_PREFIX = "occupation";
    public static final String OCCUPATION_ID = "18e14e61-495b-44cc-a7c6-df4c625934ba";
    public static final String OCCUPATION_PATH = "/occupation/" + OCCUPATION_PREFIX + ":" + OCCUPATION_ID;
    public static final String OCCUPATION_URI = "http://data.europa.eu/esco/occupation/" + OCCUPATION_ID;

    /** "midwife" — a second real occupation for list tests */
    public static final String OCCUPATION_ID_2 = "95cda6a9-a70a-4a93-9442-11d573cb4a02";
    public static final String OCCUPATION_URI_2 = "http://data.europa.eu/esco/occupation/" + OCCUPATION_ID_2;

    // ── Concepts (ISCO groups) ──────────────────────────────────────────────

    /** ISCO group "25 - Software and applications developers and analysts" */
    public static final String CONCEPT_PREFIX = "isco";
    public static final String CONCEPT_ID = "C25";
    public static final String CONCEPT_PATH = "/concept/" + CONCEPT_PREFIX + ":" + CONCEPT_ID;
    public static final String CONCEPT_URI = "http://data.europa.eu/esco/isco/" + CONCEPT_ID;

    /** A second concept for list tests */
    public static final String CONCEPT_ID_2 = "C2512";
    public static final String CONCEPT_URI_2 = "http://data.europa.eu/esco/isco/" + CONCEPT_ID_2;

    // ── Concept Schemes ─────────────────────────────────────────────────────

    /** The ESCO skills pillar concept scheme */
    public static final String CONCEPT_SCHEME_PREFIX = "concept-scheme";
    public static final String CONCEPT_SCHEME_ID = "skills";
    public static final String CONCEPT_SCHEME_PATH = "/concept-scheme/" + CONCEPT_SCHEME_PREFIX + ":" + CONCEPT_SCHEME_ID;
    public static final String CONCEPT_SCHEME_URI = "http://data.europa.eu/esco/concept-scheme/" + CONCEPT_SCHEME_ID;

    /** The ESCO occupations pillar concept scheme */
    public static final String CONCEPT_SCHEME_ID_2 = "occupations";
    public static final String CONCEPT_SCHEME_URI_2 = "http://data.europa.eu/esco/concept-scheme/" + CONCEPT_SCHEME_ID_2;

    // ── Scheme URIs (for inScheme filters) ──────────────────────────────────

    public static final String SKILLS_SCHEME_URI = "http://data.europa.eu/esco/concept-scheme/skills";
    public static final String ISCO_SCHEME_URI = "http://data.europa.eu/esco/concept-scheme/isco";
    public static final String OCCUPATIONS_SCHEME_URI = "http://data.europa.eu/esco/concept-scheme/occupations";

    // ── Nonexistent (for 404 tests) ─────────────────────────────────────────

    public static final String NONEXISTENT_ID = "00000000-0000-0000-0000-000000000000";
    public static final String NONEXISTENT_SKILL_PATH = "/skill/skill:" + NONEXISTENT_ID;
    public static final String NONEXISTENT_OCCUPATION_PATH = "/occupation/occupation:" + NONEXISTENT_ID;
    public static final String NONEXISTENT_CONCEPT_PATH = "/concept/isco:C9999";
    public static final String NONEXISTENT_CONCEPT_SCHEME_PATH = "/concept-scheme/concept-scheme:" + NONEXISTENT_ID;

    // ── Relations ───────────────────────────────────────────────────────────

    public static final String RELATION_ESSENTIAL_SKILL_FOR_OCCUPATION = "isEssentialSkillForOccupation";
    public static final String RELATION_ESSENTIAL_SKILL = "relatedEssentialSkill";
    public static final String RELATION_NARROWER_CONCEPT = "narrowerConcept";

}

