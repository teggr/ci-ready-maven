# CI Ready Maven Q&A API

The `qa-api` module is a stateless Spring Boot service that answers questions about the catalogue. It reads the catalogue Markdown during Maven's `process-classes` phase, builds a deterministic JSON index, and packages that index with the executable application. At runtime it uses only the packaged index; it does not need the source tree, GitHub Pages, a database, embeddings, or a network catalogue request.

## Requirements and build

Use Java 21 and the repository Maven Wrapper. From the repository root:

```bash
./mvnw -B -ntp -pl qa-api test
./mvnw -B -ntp -pl qa-api clean verify
```

The default catalogue directory is `static-site/_plugins/`. Override it with a Maven property; relative paths are resolved from the `qa-api` module directory:

```bash
./mvnw -B -ntp -pl qa-api clean package -Dcatalogue.source.dir=C:\path\to\catalogue
```

The indexer rejects missing, unreadable, empty, malformed and duplicate-slug sources and removes any prior generated index before failing. The output is `qa-api/target/classes/catalogue/catalogue-index.json`, which is included in the executable JAR. The Markdown `slug` is the stable index ID; catalogue URLs use the existing site convention `https://teggr.github.io/ci-ready-maven/plugins/<slug>.html`.

Build and test every module with `./mvnw -B -ntp clean verify`.

## Run locally

The application does not load `.env` files; configure the provider key and optional settings in your shell or deployment environment. Do not commit a real key. Windows PowerShell example:

```powershell
$env:CI_READY_MAVEN = "your-key"
.\mvnw.cmd -pl qa-api spring-boot:run
```

The provider credential is read from `CI_READY_MAVEN`. The model is configurable with `CI_READY_MAVEN_MODEL` (the unbenchmarked initial candidate is `gpt-5-mini`); output tokens can be set with `QA_MAX_OUTPUT_TOKENS`. No broad performance or cost benchmark results are claimed.

## API

`POST /api/v1/chat` accepts only the current question:

```json
{"question":"Which Maven plugins can enforce Java code formatting in CI?"}
```

A successful response includes the answer status, answer text, and canonical sources resolved from retrieved index records:

```json
{
  "status": "ANSWERED",
  "answer": "The catalogue describes google-java-format as suitable for CI checks.",
  "sources": [
    {
      "id": "google-java-format",
      "title": "google-java-format",
      "catalogueUrl": "https://teggr.github.io/ci-ready-maven/plugins/google-java-format.html"
    }
  ]
}
```

`INSUFFICIENT_EVIDENCE` responses explicitly state the limitation and have an empty `sources` array. Empty or invalid JSON is `400`; bodies over the configured byte limit are `413`; rate or model concurrency limits are `429`; invalid upstream model output is `502`; missing model credentials return `503`; and model-call timeouts return `504`. Errors use Spring's `ProblemDetail` format (`application/problem+json`), with the standard `type`, `title`, `status`, and `detail` fields plus a stable `code` extension, for example:

```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "The question must not be empty.",
  "code": "INVALID_REQUEST"
}
```

Error responses do not expose prompts, credentials, provider messages, or stack traces.

## Configuration and operational limits

| Setting | Environment variable | Default |
| --- | --- | --- |
| Model | `CI_READY_MAVEN_MODEL` | `gpt-5-mini` |
| Output token ceiling (reasoning + visible answer) | `QA_MAX_OUTPUT_TOKENS` | `4000` |
| Question character limit | `QA_MAX_QUESTION_LENGTH` | `1000` |
| Request body bytes | `QA_MAX_REQUEST_BODY_BYTES` | `16384` |
| Retrieved records | `QA_RETRIEVAL_LIMIT` | `5` |
| Evidence context characters | `QA_MAX_CONTEXT_CHARACTERS` | `12000` |
| Answer character limit | `QA_MAX_ANSWER_CHARACTERS` | `4000` |
| Model timeout seconds | `QA_MODEL_TIMEOUT_SECONDS` | `30` |
| OpenAI request timeout | `QA_PROVIDER_TIMEOUT` | `25s` |
| Concurrent model calls | `QA_MAX_CONCURRENT_MODEL_CALLS` | `4` |
| Queued model calls | `QA_MODEL_QUEUE_CAPACITY` | `8` |
| Per-IP requests per fixed window | `QA_REQUESTS_PER_WINDOW` | `30` |
| Rate-limit window seconds | `QA_RATE_LIMIT_WINDOW_SECONDS` | `60` |
| Allowed browser origin | `QA_CORS_ALLOWED_ORIGIN` | `https://teggr.github.io` |

Model calls are bounded to four active requests and an eight-request queue. The in-memory rate limiter uses the direct remote address and does not trust forwarding headers; it is per process and is not a substitute for a gateway limit or authentication. The endpoint has no authentication, so configure an API gateway, abuse monitoring, and stricter edge limits before exposing it publicly. CORS permits only the configured origin, `POST`/`OPTIONS`, and `Content-Type`; it does not restrict non-browser clients.

The assistant requests a typed Spring AI response and enables OpenAI JSON-object mode, then validates status and resolves cited IDs against the packaged index before returning anything.

JSON-object mode does not enforce the answer schema. If Jackson 3 (used by Spring AI) cannot deserialize a model response, the API returns `INVALID_MODEL_RESPONSE` with HTTP 502. Warnings log the sanitized field path, expected type, provider finish reason, and output character count, not the response text. A root-level failure (`response.`) can also indicate empty output; a `LENGTH` finish reason indicates the completion budget was exhausted. For reasoning models, this budget includes reasoning tokens as well as the visible answer; increase `QA_MAX_OUTPUT_TOKENS` if needed. The default is 4000 because an 800-token budget exhausted the default model's completion before it returned any JSON for a coverage/configuration question. A higher ceiling permits greater per-request cost; it does not guarantee every request will fit.

User questions and retrieved catalogue text are sent to OpenAI when a request is answerable and the provider key is configured. Questions, prompts, context and generated answers are not logged by the application. Provider-side retention and training depend on the applicable OpenAI account and terms and are not asserted here. Actuator health at `/actuator/health` reports index record count and model-key configuration without making a live provider request.

## Tests and evaluation

The default test suite has no live-model requirement. It covers fixture-based Markdown/YAML parsing, stable index generation, deterministic retrieval, source validation, request limits, CORS, and rate limiting. The `verify` lifecycle also builds and launches the packaged JAR from an isolated temporary working directory with no catalogue source and no API key, then checks the bundled index and explicit unavailable-model response. `LiveModelEvaluationTest` is opt-in and makes real paid provider calls using a fixed question set:

```bash
./mvnw -pl qa-api -Dqa.live-evaluation=true test
```

Set a valid `CI_READY_MAVEN` first. This evaluation checks expected answerability and citations and reports p50/p95 latency. It does not constitute a broad quality or token-cost benchmark; compare candidate model quality and cost before treating the configured model as final. The deterministic lexical retriever is intentionally small and the semantic correctness of generated prose cannot be guaranteed.

The later HTMX interface, browser conversation state, authentication, deployment and persistent storage remain out of scope.
