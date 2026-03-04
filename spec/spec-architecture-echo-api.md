---
title: echo-api Baseline Application Specification
version: 1.2
date_created: 2026-03-04
last_updated: 2026-03-04
owner: ranma2913
tags: [ architecture, app, design, process, infrastructure ]
---

# Introduction

This document is the baseline specification for `echo-api`, a minimal, production-ready HTTP echo service built with
Spring Boot. It is intended to serve as the authoritative reference for the application's requirements, constraints,
interfaces, and acceptance criteria — structured for both human developers and Generative AI consumption.

The `echo-api` accepts HTTP requests on any method and any path and reflects the full request back to the caller as a
structured JSON response. It is designed as a diagnostic, testing, and scaffolding tool.

---

## 1. Purpose & Scope

### Purpose

Define the baseline architecture, behavioral contracts, quality gates, and operational requirements for the `echo-api`
application as of version `0.0.2`.

### Scope

This specification covers:

- Application behavior and HTTP API contract
- Source code structure and component responsibilities
- Build, test, and coverage requirements
- Container image build and publishing process
- CI/CD pipeline structure (three workflows: `ci.yaml`, `release.yaml`, `semver-bump.yaml`)
- Security posture

### Intended Audience

- Application developers contributing to the `echo-api` repository
- Generative AI agents generating, reviewing, or modifying code within this repository
- DevOps engineers maintaining CI/CD pipelines and container releases

### Assumptions

- The application is stateless and requires no external database or message broker.
- No authentication or authorization is required for local and test environments.
- Java 25 and Apache Maven (via `./mvnw` wrapper) are the sole build-time requirements for JVM builds.
- Docker is only required for native image builds.

---

## 2. Definitions

| Term               | Definition                                                                                                   |
|--------------------|--------------------------------------------------------------------------------------------------------------|
| **Echo**           | The act of reflecting an HTTP request back to the caller as a structured JSON response                       |
| **RequestDetails** | The Java `record` that models the full HTTP request information returned by all endpoints                    |
| **Native Image**   | A GraalVM-compiled, ahead-of-time binary packaged as an OCI container image                                  |
| **OCI**            | Open Container Initiative — the standard container image format                                              |
| **Buildpacks**     | Cloud Native Buildpacks used by Spring Boot Maven Plugin to produce the container image without a Dockerfile |
| **MockMvc**        | Spring Test's server-side MVC testing framework used to test controllers without a running server            |
| **Spock**          | A Groovy-based BDD testing framework used for all test specifications in this project                        |
| **JaCoCo**         | Java Code Coverage library used to enforce coverage thresholds at build time                                 |
| **GraalVM**        | A high-performance JDK with native image compilation support                                                 |
| **GHCR**           | GitHub Container Registry — `ghcr.io` — where the Docker image is published                                  |
| **CI**             | Continuous Integration — automated build and test on every push and PR                                       |
| **CD**             | Continuous Delivery — automated container release on every push to `master`                                  |
| **CALMS**          | Culture, Automation, Lean, Measurement, Sharing — the five pillars of DevOps                                 |
| **DORA**           | DevOps Research and Assessment — defines four key software delivery performance metrics                      |
| **OWASP**          | Open Web Application Security Project — source of secure coding guidelines applied in this project           |
| **SemVer**         | Semantic Versioning — `MAJOR.MINOR.PATCH` versioning scheme used by this project                             |
| **ADR**            | Architectural Decision Record                                                                                |
| **IaC**            | Infrastructure as Code                                                                                       |
| **BDD**            | Behavior-Driven Development                                                                                  |
| **RESTful**        | Adhering to REST (Representational State Transfer) architectural principles                                  |

---

## 3. Requirements, Constraints & Guidelines

### Functional Requirements

- **REQ-001**: The application MUST accept HTTP requests on methods GET, POST, PUT, PATCH, and DELETE.
- **REQ-002**: The application MUST accept requests on any URL path, including the root path `/` and arbitrarily nested
  paths (e.g., `/a/b/c`).
- **REQ-003**: Every endpoint MUST return a JSON response containing the `RequestDetails` structure defined in Section
  4.
- **REQ-004**: GET and DELETE requests MUST return HTTP status `200 OK`.
- **REQ-005**: POST requests MUST return HTTP status `201 Created`.
- **REQ-006**: PUT and PATCH requests MUST return HTTP status `200 OK`.
- **REQ-007**: POST, PUT, and PATCH endpoints MUST accept a request body and echo it in the `body` field of the
  response.
- **REQ-008**: GET and DELETE endpoints MUST return `null` (field absent) for the `body` field.
- **REQ-009**: The application MUST log the HTTP method and full URL for every inbound request at `INFO` level.
- **REQ-010**: On startup, the application MUST print an ASCII art banner of the application name and JVM memory
  diagnostics at `INFO` level.

### Security Requirements

- **SEC-001**: All string values logged from inbound HTTP requests MUST be sanitized for newline/linefeed injection
  using OWASP `security-logging-logback`.
- **SEC-002**: No secrets, credentials, or API keys MAY be hardcoded in any source file or configuration file.
- **SEC-003**: CodeQL security analysis MUST be active on this repository. CodeQL is enabled via **GitHub's default
  setup** (configured in the repository's Security settings — Code security → CodeQL analysis), not a custom workflow
  file. No `.github/workflows/codeql.yml` exists by design; GitHub manages the scan schedule and query suite
  automatically. The default setup runs CodeQL on every push and pull request targeting the default branch.
- **SEC-004**: The application MUST NOT expose authentication-protected endpoints by default — authentication is a
  deployment concern, not a base application concern.
- **SEC-005**: GitHub Dependabot MUST be configured to automatically open pull requests for security fixes and version
  updates on a weekly schedule, covering both the `maven` and `github-actions` package ecosystems. Configuration is
  defined in `.github/dependabot.yml`.

### Coverage & Quality Requirements

- **QUA-001**: JaCoCo method coverage MUST be at or above `80%` per package. The build MUST fail if this threshold is
  not met.
- **QUA-002**: JaCoCo class coverage MUST have zero missed classes. The build MUST fail if any class is uncovered.
- **QUA-003**: All public classes MUST have at least one corresponding Spock specification.

### Build Requirements

- **BLD-001**: The project MUST build with `./mvnw verify` with no test failures or coverage violations.
- **BLD-002**: The Maven wrapper (`mvnw` / `mvnw.cmd`) MUST be the sole mechanism for invoking Maven — no system Maven
  installation is required.
- **BLD-003**: The `spring.application.name` property MUST be resolved from the Maven `artifactId` at build time using
  the `@artifactId@` token.

### Container & Deployment Requirements

- **INF-001**: The container image MUST be built using Spring Boot Buildpacks (Paketo `builder-noble-java-tiny`) — no
  `Dockerfile` is used.
- **INF-002**: The container image MUST be published to `ghcr.io/ranma2913/echo-api` on every push to `master`.
- **INF-003**: The container image MUST be a GraalVM native image compiled for `linux/amd64`.
- **INF-004**: The application MUST listen on port `8080` by default.

### Constraints

- **CON-001**: The application MUST NOT depend on a database, cache, or external service to operate.
- **CON-002**: The Java version MUST be 25.
- **CON-003**: The Spring Boot parent version MUST be 4.0.3 or compatible.
- **CON-004**: Test specifications MUST be written in Groovy using the Spock Framework — no JUnit tests.
- **CON-005**: The source layout MUST follow the standard Maven directory structure.
- **CON-006**: No `Dockerfile` MAY exist in the repository; container builds are delegated entirely to Spring Boot
  Buildpacks.

### Guidelines

- **GUD-001**: Commit messages SHOULD follow Conventional Commits with emoji as described in
  `.github/git-commit-instructions.md`.
- **GUD-002**: Pull request titles SHOULD follow the format `[component] Brief description`.
- **GUD-003**: New features SHOULD be implemented on feature branches; the `master` branch SHOULD only receive merged,
  reviewed changes.
- **GUD-004**: Code changes SHOULD be accompanied by corresponding updates to `README.md`, `DEVELOPMENT.md`, and
  `CHANGELOG.md` where applicable.
- **GUD-005**: Comments in source code SHOULD explain WHY, not WHAT, following the self-explanatory code guidelines in
  `.github/instructions/`.

### Patterns

- **PAT-001**: All controller logic MUST be implemented within `EchoApiController` as a single `@RestController` — no
  service or repository layer is required.
- **PAT-002**: Shared request-introspection logic MUST be centralized in the private `buildDetails` method of
  `EchoApiController`.
- **PAT-003**: Application startup side-effects (banner printing, diagnostics) MUST be implemented via a`@EventListener`
  on `ContextRefreshedEvent` in `EchoApiApplication`.
- **PAT-004**: Response data MUST be modeled as a Java `record` (`RequestDetails`) — no mutable DTOs.

---

## 4. Interfaces & Data Contracts

### HTTP API Contract

The controller is mapped to `{"", "/{*path}"}`, meaning every request path is handled by the same controller.

#### Endpoint Table

| HTTP Method | Path       | Request Body | Response Status | Response Body           |
|-------------|------------|--------------|-----------------|-------------------------|
| `GET`       | `/{*path}` | None         | `200 OK`        | `RequestDetails` (JSON) |
| `POST`      | `/{*path}` | Any JSON     | `201 Created`   | `RequestDetails` (JSON) |
| `PUT`       | `/{*path}` | Any JSON     | `200 OK`        | `RequestDetails` (JSON) |
| `PATCH`     | `/{*path}` | Any JSON     | `200 OK`        | `RequestDetails` (JSON) |
| `DELETE`    | `/{*path}` | None         | `200 OK`        | `RequestDetails` (JSON) |

#### `RequestDetails` Response Schema

```json
{
  "method": "string  — HTTP method (e.g., GET, POST)",
  "url": "string  — Full URL including scheme, host, path, and query string",
  "path": "string  — Path segment only, e.g. /echo or empty string for root",
  "originalUrl": "string  — Request URI with query string appended if present",
  "headers": "object  — All request headers as string key-value pairs",
  "body": "object|null — Parsed JSON request body; null/absent for GET and DELETE",
  "query": "object  — Query parameters as string key-value pairs (multi-value joined by comma)",
  "params": "object  — URI template path variables extracted by Spring MVC",
  "ip": "string  — Client remote address",
  "protocol": "string  — http or https",
  "secure": "boolean — true if the connection is HTTPS",
  "host": "string  — Value of the Host header",
  "hostname": "string  — Server name"
}
```

#### Example Request and Response

```bash
curl -s -X POST http://localhost:8080/echo \
  -H "Content-Type: application/json" \
  -d '{"hello": "world"}'
```

```json
{
  "method": "POST",
  "url": "http://localhost:8080/echo",
  "path": "/echo",
  "originalUrl": "/echo",
  "headers": {
    "content-type": "application/json",
    "host": "localhost:8080"
  },
  "body": {
    "hello": "world"
  },
  "query": {},
  "params": {},
  "ip": "127.0.0.1",
  "protocol": "http",
  "secure": false,
  "host": "localhost:8080",
  "hostname": "localhost"
}
```

### Logging Contract

Every inbound request MUST produce the following log line at `INFO` level before the response is built:

```
Endpoint called: method=<HTTP_METHOD> url=<SANITIZED_FULL_URL>
```

The URL value MUST be sanitized via `org.owasp.security.logging.Utils.escapeNLFChars` before logging.

---

## 5. Acceptance Criteria

- **AC-001**: Given any HTTP GET to any path, When the request is received, Then the response status is `200 OK` and the
  `method` field in the JSON body is `"GET"`.
- **AC-002**: Given an HTTP POST with a JSON body to any path, When the request is received, Then the response status is
  `201 Created` and the `body` field reflects the submitted JSON.
- **AC-003**: Given an HTTP PUT or PATCH with a JSON body to any path, When the request is received, Then the response
  status is `200 OK` and the `body` field reflects the submitted JSON.
- **AC-004**: Given an HTTP DELETE to any path, When the request is received, Then the response status is `200 OK` and
  the `body` field is absent from the response.
- **AC-005**: Given a GET request with query parameters (e.g., `?foo=bar`), When the request is received, Then the
  `query` field in the response maps each parameter name to its value.
- **AC-006**: Given a GET request with a custom request header, When the request is received, Then that header appears
  in the `headers` map of the response.
- **AC-007**: Given a request to a nested path (e.g., `/a/b/c`), When the request is received, Then the `path` field in
  the response equals `/a/b/c`.
- **AC-008**: Given a GET request to the root path `/`, When the request is received, Then the `path` field in the
  response equals `/`.
- **AC-009**: Given the application is started, When the context is fully refreshed, Then an ASCII art banner of the
  application name and JVM memory diagnostics appear in the log at `INFO` level.
- **AC-010**: Given `./mvnw verify` is executed, When all tests pass, Then JaCoCo reports ≥80% method coverage per
  package and 0 missed classes; the build exits with code `0`.
- **AC-011**: Given a push to `master`, When the CI/CD pipeline runs, Then a GraalVM native OCI image is built and
  published to `ghcr.io/ranma2913/echo-api`.
- **AC-012**: Given a push or PR to the default branch, When the CI/CD pipeline runs, Then GitHub's default CodeQL setup
  automatically performs security analysis. No custom `codeql.yml` workflow file is required — this is fulfilled by the
  repository's GitHub-managed CodeQL default setup.

---

## 6. Test Automation Strategy

- **Test Levels**: Integration tests only (no unit tests in isolation) — all tests exercise the full Spring context via
  MockMvc.
- **Frameworks**: Spock Framework 2.4 (Groovy 5.0) with `spock-spring` for Spring Boot integration;
  `spring-boot-starter-webmvc-test` provides MockMvc.
- **Test Naming Convention**: All specification files MUST end with `Spec.groovy` and be discovered by Maven Surefire
  via the `**/*Spec.java` include pattern (Surefire compiles Groovy specs to `.class` files discoverable as `.java`).
  The Surefire configuration also includes `**/*Test.java` for any future JUnit-style tests, though only Spock specs
  exist today.
- **Test Data Management**: Tests use inline fixture data — no external files or databases. MockMvc performs requests
  against a fully loaded Spring context without a running server.
- **CI/CD Integration**: `./mvnw verify` runs all Spock specs via the `gmavenplus-plugin` and Maven Surefire on every CI
  run. JaCoCo gates are enforced at the `verify` phase.
- **Coverage Requirements**: ≥80% method coverage per package; 0 missed classes (enforced by JaCoCo `check` goal at
  `verify` phase).
- **Performance Testing**: Not in scope for the current baseline. The application's lightweight nature (no I/O, no DB)
  makes request-level performance testing low priority.
- **Surefire Heap**: Surefire is allocated 2048m (`-Xmx2048m`) to accommodate Groovy/Spock compilation overhead.

---

## 7. CI/CD Pipeline Structure

Three GitHub Actions workflows govern the build, release, and versioning lifecycle. CodeQL security analysis is handled
separately by GitHub's default setup (see SEC-003) — no custom CodeQL workflow file exists in this repository.

### `ci.yaml` — Continuous Integration

- **Triggers**: Every push to any branch; pull requests targeting `master`; manual dispatch.
- **Concurrency**: Cancels any in-progress run for the same ref on new pushes.
- **Steps**:
  1. Checkout (full depth for accurate git history)
  2. Set up Java 25 (Temurin distribution)
  3. Restore/save Maven dependency cache
  4. Run `./mvnw -B -ntp verify -Djacoco.haltOnFailure=false`
  5. On pull requests: post a JaCoCo coverage comment via `madrapps/jacoco-report` (thresholds: overall ≥75%, changed
     files ≥90%)
  6. Write a GitHub Step Summary with build and coverage results

### `release.yaml` — Native Image Build & Publish

- **Triggers**: Every push to `master`; manual dispatch.
- **Concurrency**: Cancels any in-progress run for the same ref.
- **Steps**:
  1. Checkout, Java setup, Maven cache restore
  2. Resolve the image tag via `git describe --tags --match "v[0-9]*.[0-9]*.[0-9]*"` (falls back to `v0.0.0`)
  3. Run `./mvnw -B verify` with `-Dspring-boot.build-image.imageName` and `-Dspring-boot.build-image.tags=…:latest` to
     build and tag the GraalVM native OCI image
  4. Inspect the built image and log metadata
  5. Authenticate with GHCR using `docker/login-action`
  6. Push all tags (`docker image push --all-tags`)
  7. Write a GitHub Step Summary with build and push results
- **Image Tags**: The versioned tag is derived from the most recent git tag (e.g., `v0.0.2`); the `latest` tag is always
  updated.

### `semver-bump.yaml` — Semantic Version Bump

- **Triggers**: Pull requests targeting any branch except `master`; manual dispatch with explicit `bump-level` choice (
  `patch`, `minor`, `major`).
- **Purpose**: Automates `pom.xml` version bumping and git tagging on feature branches.
- **Steps**:
  1. Lookup the last git tag via `git describe`
  2. Compute the next version using `olegsu/semver-action`
  3. Run `./mvnw versions:set` to update `pom.xml`
  4. Commit the version bump back to the feature branch
  5. Create an annotated git tag for the new version

---

## 8. Rationale & Context

### Why a universal wildcard mapping?

Mapping the controller to `{"", "/{*path}"}` allows `echo-api` to function as a drop-in HTTP sink for any path. This is
its primary use case: integration testing, webhook inspection, HTTP client debugging, and scaffolding demonstrations.

### Why Spock/Groovy for tests?

Spock provides expressive BDD-style `given/when/then` test blocks and data-driven `where` tables. Groovy's dynamic
typing and concise syntax reduce test boilerplate while `spock-spring` provides first-class Spring Boot test
integration.

### Why GraalVM native image?

Native images produce small, fast-starting containers with low memory footprint — well suited for a diagnostic utility
that may be spun up and torn down frequently in development or staging environments.

### Why Buildpacks instead of Dockerfile?

Spring Boot Buildpacks (Paketo) handle base image selection, JVM/native toolchain installation, and image layering
automatically. This eliminates Dockerfile maintenance burden and ensures best practices (non-root user, minimal layers)
are applied by default.

### Why OWASP `security-logging-logback`?

Without log sanitization, an attacker can inject newline characters into a logged URL to forge log entries (log
injection, OWASP A03). The OWASP logging library escapes these characters automatically.

---

## 9. Dependencies & External Integrations

### External Systems

- **EXT-001**: GitHub — source repository, CI/CD (GitHub Actions), container registry (GHCR), and CodeQL security
  scanning via GitHub's default setup (configured in repository Security settings, not a custom workflow file).
- **EXT-002**: GitHub Dependabot — automated dependency update service configured in `.github/dependabot.yml`. Monitors
  two ecosystems on a weekly schedule: `maven` (root `pom.xml`) and `github-actions` (workflow files). Opens pull
  requests automatically for security fixes and version updates.

### Third-Party Services

- **SVC-001**: GitHub Container Registry (`ghcr.io`) — OCI image storage and distribution. Images are pushed on every
  `master` build.
- **SVC-002**: Paketo Buildpacks Builder (`paketobuildpacks/builder-noble-java-tiny`) — native image compilation and OCI
  packaging during release builds.

### Infrastructure Dependencies

- **INF-001**: Java 25 runtime — required at build time and embedded in the OCI image via the native image compilation
  step.
- **INF-002**: Docker daemon — required locally and in CI for native OCI image builds (`spring-boot:build-image` goal).
- **INF-003**: Port `8080` — the application binds to this port by default; must be available in any deployment
  environment.

### Data Dependencies

- **DAT-001**: None. The application is fully stateless and requires no external data sources.

### Technology Platform Dependencies

- **PLT-001**: Java 25 — required for language features and JVM bytecode compatibility.
- **PLT-002**: Spring Boot 4.0.3 — parent POM provides dependency management for all Spring and third-party libraries.
- **PLT-003**: Apache Groovy 5.0.4 — required for Spock test compilation via `gmavenplus-plugin`.
- **PLT-004**: GraalVM Native Maven Plugin (`native-maven-plugin`) — required for native image compilation; the release
  workflow passes image name parameters directly to `./mvnw verify` rather than using a separate named Maven profile.
- **PLT-005**: `io.leego:banana` v2.1.0 — ASCII art font rendering library used by `EchoApiApplication` to print the
  startup banner (REQ-010).
- **PLT-006**: Lombok — compile-time annotation processor used for boilerplate reduction; declared as an optional
  dependency and wired into `maven-compiler-plugin` as an annotation processor path.
- **PLT-007**: `spring-boot-devtools` — runtime-optional dependency that provides automatic application restarts and
  live-reload during local development; excluded from production artifacts.

### Compliance Dependencies

- **COM-001**: OWASP Top 10 — secure coding guidelines applied throughout the codebase, particularly A03 (Injection) via
  log sanitization and A05 (Security Misconfiguration) via GitHub's default CodeQL setup enforcing continuous security
  analysis.

---

## 10. Examples & Edge Cases

### Edge Case: Root path `/`

A GET to `/` still matches the `/{*path}` mapping. Spring MVC resolves the path variable to `/`, not an empty string.

```bash
curl http://localhost:8080/
# Response: { "path": "/", ... }
```

### Edge Case: Multi-value query parameters

The `query` map joins multiple values for the same key with a comma.

```bash
curl "http://localhost:8080/echo?tag=a&tag=b"
# Response: { "query": { "tag": "a,b" }, ... }
```

### Edge Case: Request with no `Host` header

When no `Host` header is present, `host` will be `null` and `hostname` reflects the server's configured name (
`localhost` by default).

### Edge Case: POST with empty body

If a POST request is sent with an empty JSON body `{}`, the `body` field in the response will be an empty JSON object,
not `null`.

```bash
curl -X POST http://localhost:8080/echo \
  -H "Content-Type: application/json" \
  -d '{}'
# Response: { "body": {}, ... }
```

### Edge Case: Deeply nested path

```bash
curl http://localhost:8080/a/b/c/d/e
# Response: { "path": "/a/b/c/d/e", "url": "http://localhost:8080/a/b/c/d/e", ... }
```

---

## 11. Validation Criteria

The following criteria MUST be satisfied for a build to be considered compliant with this specification:

- **VAL-001**: `./mvnw verify` exits with code `0` — all Spock specs pass and all JaCoCo thresholds are met.
- **VAL-002**: `EchoApiControllerSpec` covers all five HTTP methods (GET, POST, PUT, PATCH, DELETE) with at least one
  passing test each.
- **VAL-003**: `EchoApiApplicationSpec` contains a Spring context smoke test that asserts the application context loads
  successfully.
- **VAL-004**: The `RequestDetails` response JSON contains all 13 fields defined in Section 4 for methods that include a
  body (POST, PUT, PATCH).
- **VAL-005**: The `RequestDetails` response JSON omits or nullifies the `body` field for GET and DELETE requests.
- **VAL-006**: GitHub's default CodeQL setup reports zero findings of severity HIGH or CRITICAL against the
  `security-extended` and `security-and-quality` query suites. Results are visible in the repository's Security → Code
  scanning alerts page.
- **VAL-007**: The container image published to GHCR is tagged with the git-derived version tag (e.g., `v0.0.2` resolved
  via `git describe`) and `latest`, and runs without error on
  `docker run --rm -p 8080:8080 ghcr.io/ranma2913/echo-api:latest`.
- **VAL-008**: A GET to `http://localhost:8080/echo` returns `200 OK` with `Content-Type: application/json` in the
  response headers.

---

## 12. Related Specifications / Further Reading

- [README.md](../README.md) — Project overview, technology stack, API reference, and CI/CD summary
- [DEVELOPMENT.md](../DEVELOPMENT.md) — Local setup, development workflow, build commands, coding standards
- [.github/instructions/security-and-owasp.instructions.md](../.github/instructions/security-and-owasp.instructions.md) —
  OWASP Top 10 secure coding guidelines applied in this project
- [.github/instructions/devops-core-principles.instructions.md](../.github/instructions/devops-core-principles.instructions.md) —
  CALMS and DORA metrics guidance
- [Spring Boot Reference Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spock Framework Documentation](https://spockframework.org/spock/docs/2.4/index.html)
- [GraalVM Native Image Documentation](https://www.graalvm.org/latest/reference-manual/native-image/)
- [Paketo Buildpacks](https://paketo.io/)
- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Conventional Commits](https://www.conventionalcommits.org/)

