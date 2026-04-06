# ESCO Open API — Golden Master Test Suite

Regression test suite for the [ESCO Open API](https://ec.europa.eu/esco/open-api/v3/api-docs) (v2.1.1).

**First run** captures every endpoint's response as a baseline file.  
**Subsequent runs** compare live responses against those baselines, flagging structural drift, data changes, and performance regressions.

## How It Works

The suite operates in two modes, controlled by `esco.mode`:

| Mode | Behaviour |
|------|-----------|
| **capture** | Calls the API, saves the response as a baseline JSON file. Always passes. |
| **auto** *(default)* | If a baseline exists → compare (fail on diff). Otherwise → capture. |

Each baseline is a JSON file stored at `src/test/resources/baselines/{group}/{TestClass}/{testMethod}.json` containing:

```json
{
  "capturedAt": "2026-04-03T10:15:30Z",
  "capturedBaseUrl": "https://ec.europa.eu/esco/open-api",
  "responseTimeMs": 234,
  "httpStatus": 200,
  "body": { "..." }
}
```

### Comparison Rules

- **`href` normalization:** `href` values in `_links` are fully compared, but the deployment base URL is replaced with a placeholder before comparison. This means the link *paths and query strings* are validated while allowing the suite to run against alternative deployments under a different root URL.
- **Ignored:** `logref` (request-specific, changes on every call)
- **Lenient array order:** all arrays
- **Exact match:** everything else
- **Non-JSON responses** (RDF/XML, Turtle): exact string match
- **Status code:** must match exactly
- **Performance:** `actual ≤ baseline × 3` (200% threshold — tuned for remote public API)

### Known API Bugs (`baselines/bugs/`)

Some error-handling tests reveal cases where the API deviates from its own OpenAPI spec. These are baselined under `baselines/bugs/` (instead of `baselines/error/`) to separate known-incorrect behaviour from correct behaviour:

| Test | Spec says | API actually returns |
|------|-----------|---------------------|
| `occupation_history_not_found` | 404 | 500 (`NotFoundException`) |
| `skill_conversion_not_found` | 404 | 500 (`NotFoundException`) |
| `concept_related_not_found` | 404 | 200 (empty results — doesn't validate parent existence) |

These baselines track the *actual* behaviour so regressions in the bug itself are still detected. If the API is ever fixed, the baselines should be recaptured.

## Coverage

~104 test cases covering all 34 API operations:

| Group | What |
|-------|------|
| resource | Single GET for skill, occupation, concept, concept-scheme (en, pt, pinned version) |
| contentneg | RDF/XML + Turtle for all 4 resource types |
| related | `/related` + `/related-hierarchy` with relation, inScheme, pagination |
| hierarchy | Per-resource `/hierarchy` + full-hierarchy with viewObsolete |
| list | By-URIs + by-scheme with pagination and viewObsolete |
| search | GET (text, type, facets, hasLabel, hasLabelRole, inScheme, pagination, viewObsolete) + POST (all SearchFilter fields) |
| history | `/history` (with fullHistory) + `/conversion` per type |
| config | `/config-info/prefixes`, `/default-version`, `/available-languages` |
| skillgroups | `/api/skill-groups` |
| error | 404 (nonexistent resources) + 400 (missing/invalid params) |
| bugs | API-spec deviations (see above) |

## Quick Start

```bash
# First-time capture
./mvnw test -Desco.mode=capture

# Daily run (auto mode — default)
./mvnw test

# Test against a replacement API
./mvnw test -Desco.api.baseUrl=http://localhost:8080/api
```

## Running Specific Groups

```bash
./mvnw test -Dtest="**/resource/*Test"
./mvnw test -Dtest="**/search/*Test"
./mvnw test -Dtest="**/error/*Test"
```

## Recapture

After an intentional API change, delete baselines and recapture:

```bash
rm -rf src/test/resources/baselines
./mvnw test -Desco.mode=capture
```

## Configuration

All properties in `src/test/resources/esco-test.properties` are overridable via `-D`:

| Property | Default | Description |
|----------|---------|-------------|
| `esco.api.baseUrl` | `https://ec.europa.eu/esco/open-api` | API base URL |
| `esco.dataset.version` | `v1.2.1` | ESCO dataset version |
| `esco.mode` | `auto` | `capture` or `auto` |
| `esco.throttle.delayMs` | `500` | Delay between requests (rate-limit protection) |
| `esco.performance.thresholdPercent` | `200` | Max allowed response-time increase vs baseline |

## Tech Stack

Java 21, Maven, JUnit 5, Jackson, JsonUnit, AssertJ, `java.net.http.HttpClient`. No DI framework.
