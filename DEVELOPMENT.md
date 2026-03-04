# Development Guide

Local development reference for the `echo-api` project. For a project overview, see the [README](README.md).

---

## Table of Contents

- [Prerequisites](#prerequisites)
- [Project Structure](#project-structure)
- [Local Setup](#local-setup)
- [Development Workflow](#development-workflow)
- [Testing](#testing)
- [Build and Deployment](#build-and-deployment)
- [Coding Standards](#coding-standards)
- [Troubleshooting](#troubleshooting)

---

## Prerequisites

- **Java 21** (Temurin recommended) — `java -version` must report `21.x`
- **Docker** (only required for native image builds)
- Maven is provided via the `./mvnw` wrapper — no separate Maven installation needed

---

## Project Structure

```
echo-api/
├── src/
│   ├── main/java/com/ranma2913/echoapi/
│   │   ├── EchoApiApplication.java   # Spring Boot entry point; startup banner & memory diagnostics
│   │   └── EchoApiController.java    # /echo endpoint (GET, PUT, PATCH, POST, DELETE)
│   ├── main/resources/config/
│   │   └── application.properties   # App name, logging pattern, log levels
│   └── test/groovy/com/ranma2913/echoapi/
│       ├── EchoApiControllerSpec.groovy  # MockMvc integration tests for all endpoints
│       └── EchoApiApplicationSpec.groovy # Spring context smoke test
├── .github/
│   ├── workflows/                    # ci.yaml, release.yaml, semver-bump.yaml
│   ├── instructions/                 # GitHub Copilot instruction files
│   ├── prompts/                      # GitHub Copilot prompt files
│   ├── agents/                       # GitHub Copilot agent files
│   └── skills/                       # GitHub Copilot skill files
├── pom.xml                           # Maven build — Spring Boot 4, Java 21, Spock, JaCoCo, GraalVM
├── mvnw / mvnw.cmd                   # Maven wrapper scripts
├── AGENTS.md                         # AI agent context and instructions
├── llms.txt                          # LLM-friendly project index
└── README.md                         # Project overview
```

**Key source files:**

- `EchoApiApplication.java` — Spring Boot entry point; prints ASCII banner (via Banana) and JVM memory diagnostics on
  startup via `ContextRefreshedEvent`
- `EchoApiController.java` — `@RestController` mapped to `{"", "/{*path}"}`, handles all five HTTP methods
- `application.properties` — application name, console log pattern, and log-level overrides

---

## Local Setup

```bash
# 1. Clone the repository
git clone https://github.com/ranma2913/echo-api.git
cd echo-api

# 2. Verify Java version
java -version

# 3. Install dependencies and compile (no tests)
./mvnw -B compile

# 4. Compile test sources
./mvnw -B test-compile
```

No external database or environment variables are required for local development.

---

## Development Workflow

```bash
# Start the application (hot-reload via spring-boot-devtools)
./mvnw spring-boot:run
# API is available at: http://localhost:8080
```

Editing a `.java` file while `spring-boot:run` is active triggers automatic class reloading via `spring-boot-devtools`.

### Branching & Versioning

- **`master`** — production branch; triggers Docker image release on every push
- **Feature branches** — automatically receive a patch semver bump when a PR is opened (via `semver-bump.yaml`)
- Use the `semver-bump.yaml` workflow dispatch to manually trigger a `minor` or `major` version bump
- Commit messages follow [Conventional Commits](https://www.conventionalcommits.org/) with emoji (see
  `.github/git-commit-instructions.md`)

---

## Testing

Tests are written in **Groovy 5 / Spock 2.4** and use Spring's `MockMvc` for full integration-style testing without
starting an actual HTTP server.

### Running Tests

```bash
# Run all tests (JVM mode) — generates JaCoCo coverage report
./mvnw test

# Run tests in GraalVM native-test mode (matches CI)
./mvnw -B test -Pnative -Djacoco.haltOnFailure=false

# Run a single spec by name
./mvnw test -Dtest=EchoApiControllerSpec

# Open the JaCoCo HTML coverage report
open target/site/jacoco/index.html
```

### Coverage Requirements (enforced — build fails if violated)

| Metric           | Threshold |
|------------------|-----------|
| Method coverage  | ≥ 80%     |
| Missed classes   | 0         |
| CI overall       | ≥ 75%     |
| CI changed files | ≥ 90%     |

### Test Files

| File                            | Purpose                                     |
|---------------------------------|---------------------------------------------|
| `EchoApiControllerSpec.groovy`  | MockMvc integration tests for all endpoints |
| `EchoApiApplicationSpec.groovy` | Spring context smoke test                   |

Test class names must end with `Spec` or `Test` to be picked up by Maven Surefire.

---

## Build and Deployment

### JVM JAR

```bash
./mvnw -B package
# Output: target/echo-api-<version>.jar
```

### GraalVM Native Image (OCI Container)

Requires Docker. Uses Spring Boot Buildpacks — no Dockerfile needed.

```bash
# Build native OCI image (tagged ranma2913/echo-api:latest)
./mvnw -B package -Pnative

# Inspect the built image
docker image inspect ranma2913/echo-api:latest

# Run the native image
docker run --rm -p 8080:8080 ranma2913/echo-api:latest
```

---

## Coding Standards

- **Production code:** Java 21 — use `@RestController`, `@RequestMapping`, `@ResponseStatus`
- **Test code:** Groovy 5 / Spock — specs named `*Spec.groovy`
- **Boilerplate reduction:** Prefer Lombok annotations (`@Slf4j`, `@SneakyThrows`)
- **Logging:** SLF4J via `@Slf4j` — never use `System.out.println`
- **Comments:** Explain *why*, not *what* — see `.github/instructions/self-explanatory-code-commenting.instructions.md`
- **No wildcard imports** in Java production code
- **Formatting:** Standard IntelliJ IDEA defaults
- **Security:** No hardcoded secrets; follow OWASP Top 10 (see
  `.github/instructions/security-and-owasp.instructions.md`)

For deeper AI-assisted development guidance, see [`AGENTS.md`](AGENTS.md) and [
`.github/instructions/`](.github/instructions/).

---

## Troubleshooting

**Application fails to start**

- Confirm `java -version` returns Java 21
- Check `src/main/resources/config/application.properties` for misconfiguration

**Tests fail with coverage violation**

- Run `./mvnw test` and open `target/site/jacoco/index.html` to identify uncovered methods
- All classes must have at least one test; add a Spock spec if a new class is introduced

**Native image build fails**

- Ensure Docker daemon is running
- GraalVM reachability metadata is maintained under `target/graalvm-reachability-metadata/`
-
Check [Spring Boot Native documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/native-image.html)
for reflection/proxy configuration hints

**Logging**

- Log pattern: `%date{yyyy-MM-dd HH:mm:ss.SSS} [thread] LEVEL logger.method:line :: message`
- Adjust log levels in `application.properties` using `logging.level.<package>=DEBUG|INFO|WARN`

