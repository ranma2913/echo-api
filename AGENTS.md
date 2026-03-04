# AGENTS.md

> Context and instructions for AI coding agents working on the `echo-api` project.

## Project Overview

`echo-api` is a minimal, production-ready Spring Boot REST API scaffold. It exposes a single `/echo` endpoint that
accepts all standard HTTP methods (GET, PUT, PATCH, POST, DELETE) and echoes the request body back to the caller. Its
primary purpose is to serve as a reference scaffold demonstrating:

- Spring Boot 4 + Java 25 REST API patterns
- GraalVM native image compilation via Spring Boot Maven plugin
- Spock/Groovy integration tests with MockMvc
- JaCoCo code coverage enforcement (≥80% method coverage, 0 missed classes)
- Automated CI/CD via GitHub Actions (build, test, CodeQL, Docker release, semver bumping)
- GitHub Copilot customization via instructions, prompts, agents, and skills

**Key technologies:** Java 25, Spring Boot 4, Spring MVC, Lombok, GraalVM Native Image, Spock 2.4, Groovy 5, JaCoCo,
Maven Wrapper (`mvnw`)

**Docker image:** `ghcr.io/ranma2913/echo-api`

---

## Setup Commands

Prerequisites: Java 25 (Temurin recommended), Maven is provided via the wrapper (`./mvnw`).

```bash
# Verify Java version (must be 25)
java -version

# Install dependencies and compile (no tests)
./mvnw -B compile

# Install dependencies and compile test sources
./mvnw -B test-compile
```

No external database or environment variables are required for local development.

---

## Development Workflow

```bash
# Start the application locally (JVM mode, hot-reload via spring-boot-devtools)
./mvnw spring-boot:run

# The API is available at:
# http://localhost:8080/echo
```

The `spring-boot-devtools` dependency is included, so saving a Java file triggers automatic class reloading during
`spring-boot:run`.

**Key source files:**

- `src/main/java/com/ranma2913/echoapi/EchoApiApplication.java` — Spring Boot entry point; prints ASCII banner and
  memory diagnostics on startup
- `src/main/java/com/ranma2913/echoapi/EchoApiController.java` — REST controller for all `/echo` methods
- `src/main/resources/config/application.properties` — Application and logging configuration

---

## Testing Instructions

Tests are written in **Groovy/Spock** and live under `src/test/groovy/`.

```bash
# Run all tests (JVM mode) with JaCoCo coverage
./mvnw test


# Run a single test class by name
./mvnw test -Dtest=EchoApiControllerSpec

# View JaCoCo HTML coverage report after running tests
open target/site/jacoco/index.html
```

**Test file locations and conventions:**

- `src/test/groovy/com/ranma2913/echoapi/EchoApiControllerSpec.groovy` — MockMvc integration tests for all `/echo`
  endpoints
- `src/test/groovy/com/ranma2913/echoapi/EchoApiApplicationSpec.groovy` — Spring context smoke test
- Test classes must end with `Spec` or `Test` to be picked up by Surefire

**Coverage requirements (enforced by JaCoCo; build fails if violated):**

- Minimum **80% method coverage** per package
- Maximum **0 missed classes** per package

---

## Code Style Guidelines

- **Language:** Java 25 for production code; Groovy 5 for test code
- **Framework:** Spring Boot 4 / Spring MVC — use `@RestController`, `@RequestMapping`, `@ResponseStatus`
- **Annotations:** Prefer Lombok (`@Slf4j`, `@SneakyThrows`) to reduce boilerplate
- **Logging:** Use SLF4J via the Lombok `@Slf4j` annotation; never use `System.out`
- **Naming:** Follow standard Java naming conventions; test specs named `*Spec.groovy`
- **Comments:** Write self-documenting code; comment only to explain *why*, not *what* (see
  `.github/instructions/self-explanatory-code-commenting.instructions.md`)
- **No wildcard imports** in Java production code
- **Formatting:** Standard IntelliJ IDEA defaults; no enforced linter beyond compiler warnings

---

## Build and Deployment

### JVM JAR build

```bash
./mvnw -B package
# Output: target/echo-api-<version>.jar
```

### GraalVM Native Image (OCI container)

The `native` Maven profile builds a GraalVM native image packaged as a Docker/OCI image using Spring Boot's
`build-image-no-fork` goal (Buildpacks, no Dockerfile required).

```bash
# Build native OCI image locally (requires Docker daemon)
./mvnw -B package
# Tagged as: ghcr.io/ranma2913/echo-api:local
```

```bash
# Inspect the built image
docker image inspect ghcr.io/ranma2913/echo-api:local

# Run the native image container
docker run --rm -p 8080:8080 ghcr.io/ranma2913/echo-api:local
```

### CI/CD Pipelines (`.github/workflows/`)

| Workflow           | Trigger                   | What it does                                                                                            |
|--------------------|---------------------------|---------------------------------------------------------------------------------------------------------|
| `ci.yaml`          | push / PR / manual        | Runs `mvnw verify`, executes Spock tests, generates JaCoCo report, runs CodeQL security analysis        |
| `release.yaml`     | push to `master` / manual | Builds & pushes GraalVM native Docker image to GitHub Container Registry (`ghcr.io/ranma2913/echo-api`) |
| `semver-bump.yaml` | PR to non-master / manual | Bumps patch/minor/major version in `pom.xml`, commits, and tags                                         |

**Required GitHub secrets/variables for release:**

- `GITHUB_TOKEN` (built-in) — automatically provided; used to push to `ghcr.io` and comment on PRs

---

## Pull Request Guidelines

- **Title format:** `[component] Brief description` — e.g., `[controller] Add HEAD method support`
- **Before submitting:** ensure `./mvnw test` passes locally with no JaCoCo violations
- **Coverage gate:** CI enforces ≥75% overall coverage and ≥90% coverage on changed files (via `madrapps/jacoco-report`)
- **Semver:** PRs to non-master branches automatically trigger a patch version bump; use `semver-bump.yaml` workflow
  dispatch for minor/major bumps
- **CodeQL:** Security analysis runs automatically on every PR; address any `security-extended` findings before merging

---

## Security Considerations

- No hardcoded secrets anywhere in source code — use environment variables or GitHub Actions secrets
- CodeQL `security-extended,security-and-quality` queries run in CI on every push/PR
- Follow OWASP Top 10 guidelines (see `.github/instructions/security-and-owasp.instructions.md`)
- The API has no authentication by default — add Spring Security if exposing beyond local/test environments

---

## Debugging and Troubleshooting

**Application fails to start:**

- Confirm `java -version` returns Java 25
- Check `src/main/resources/config/application.properties` for misconfiguration

**Tests fail with coverage violation:**

- Run `./mvnw test` and open `target/site/jacoco/index.html` to identify uncovered methods
- All classes must have at least one test; add a Spock spec if a new class is introduced

**Native image build fails:**

- Ensure Docker daemon is running
- GraalVM reachability metadata is maintained under `target/graalvm-reachability-metadata/`
- Check Spring Boot Native documentation for hints on reflection/proxy config

**Logging:**

- Log pattern: `%date{yyyy-MM-dd HH:mm:ss.SSS} [thread] LEVEL logger.method:line :: message`
- Adjust log levels in `application.properties` using `logging.level.<package>=DEBUG|INFO|WARN`

---

## Repository Structure

```
echo-api/
├── src/
│   ├── main/java/com/ranma2913/echoapi/
│   │   ├── EchoApiApplication.java   # Entry point, startup banner, memory diagnostics
│   │   └── EchoApiController.java    # /echo endpoint (GET, PUT, PATCH, POST, DELETE)
│   ├── main/resources/config/
│   │   └── application.properties   # App & logging config
│   └── test/groovy/com/ranma2913/echoapi/
│       ├── EchoApiControllerSpec.groovy  # MockMvc integration tests
│       └── EchoApiApplicationSpec.groovy # Context smoke test
├── .github/
│   ├── workflows/                    # ci.yaml, release.yaml, semver-bump.yaml
│   ├── instructions/                 # GitHub Copilot instruction files
│   ├── prompts/                      # GitHub Copilot prompt files
│   ├── agents/                       # GitHub Copilot agent files
│   └── skills/                       # GitHub Copilot skill files
├── pom.xml                           # Maven build — Spring Boot 4, Java 25, Spock, JaCoCo, GraalVM
├── mvnw / mvnw.cmd                   # Maven wrapper scripts
├── AGENTS.md                         # This file
├── llms.txt                          # LLM-friendly project index
└── README.md                         # Human-facing project overview
```

---

## GitHub Copilot Customization

This repo ships with extensive Copilot customization. Relevant instruction files agents should be aware of:

| File                                                                    | Purpose                                                        |
|-------------------------------------------------------------------------|----------------------------------------------------------------|
| `.github/instructions/taming-copilot.instructions.md`                   | Core directives: surgical changes only, preserve existing code |
| `.github/instructions/java.instructions.md`                             | Java coding standards and best practices                       |
| `.github/instructions/security-and-owasp.instructions.md`               | OWASP Top 10 secure coding rules                               |
| `.github/instructions/performance-optimization.instructions.md`         | Backend performance best practices                             |
| `.github/instructions/self-explanatory-code-commenting.instructions.md` | Comment style: explain *why*, not *what*                       |
| `.github/instructions/devops-core-principles.instructions.md`           | CALMS framework and DORA metrics guidance                      |

