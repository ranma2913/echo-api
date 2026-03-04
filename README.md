# echo-api

[![CI](https://github.com/ranma2913/echo-api/actions/workflows/ci.yaml/badge.svg)](https://github.com/ranma2913/echo-api/actions/workflows/ci.yaml)
[![Release](https://github.com/ranma2913/echo-api/actions/workflows/release.yaml/badge.svg)](https://github.com/ranma2913/echo-api/actions/workflows/release.yaml)
[![Docker Image](https://img.shields.io/badge/ghcr.io-ranma2913%2Fecho--api-blue)](https://github.com/ranma2913/echo-api/pkgs/container/echo-api)
[![Java](https://img.shields.io/badge/Java-25-blue)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.3-brightgreen)](https://spring.io/projects/spring-boot)

A minimal, production-ready Spring Boot REST API that echoes HTTP requests back to the caller. Send any HTTP method to
any path and receive a structured JSON response containing the method, URL, headers, body, query parameters, and
connection details — no configuration required.

---

## Table of Contents

- [Key Features](#key-features)
- [Technology Stack](#technology-stack)
- [Project Architecture](#project-architecture)
- [Getting Started](#getting-started)
- [API Reference](#api-reference)
- [CI/CD Pipelines](#cicd-pipelines)
- [Contributing](#contributing)
- [Security](#security)
- [License](#license)

---

## Key Features

- **Universal echo endpoint** — accepts GET, PUT, PATCH, POST, and DELETE on any path (e.g., `/echo`, `/foo/bar/baz`)
- **Full request introspection** — returns method, full URL, path, original URL, headers, body, query params, path
  variables, IP, protocol, host, and hostname
- **GraalVM Native Image** — built as a lean OCI container via Spring Boot Buildpacks (no Dockerfile needed)
- **Spock/Groovy integration tests** — all endpoints covered with MockMvc-based `*Spec.groovy` tests
- **JaCoCo coverage enforcement** — build fails if method coverage drops below 80% or any class is missed
- **Automated CI/CD** — GitHub Actions handles build, test, CodeQL security analysis, Docker release, and semver
  versioning
- **ASCII startup banner** — prints the application name in ASCII art and memory usage on startup
- **GitHub Copilot customization** — ships with instructions, prompts, agents, and skills for AI-assisted development

---

## Technology Stack

| Category           | Technology                                               | Version        |
|--------------------|----------------------------------------------------------|----------------|
| Language           | Java                                                     | 25             |
| Framework          | Spring Boot                                              | 4.0.3          |
| Web Layer          | Spring MVC (`spring-boot-starter-webmvc`)                | —              |
| Build Tool         | Apache Maven (via `mvnw` wrapper)                        | —              |
| Test Language      | Groovy (Apache Groovy)                                   | 5.0.4          |
| Test Framework     | Spock Framework                                          | 2.4-groovy-5.0 |
| Test Runner        | Maven Surefire Plugin                                    | —              |
| Code Coverage      | JaCoCo                                                   | 0.8.14         |
| Boilerplate        | Lombok                                                   | —              |
| ASCII Art          | Banana (io.leego)                                        | 2.1.0          |
| Native Image       | GraalVM Native Maven Plugin                              | —              |
| Container Build    | Spring Boot Buildpacks                                   | —              |
| Container Registry | GitHub Container Registry (`ghcr.io/ranma2913/echo-api`) | —              |
| CI/CD              | GitHub Actions                                           | —              |
| Security Analysis  | GitHub CodeQL                                            | —              |

---

## Project Architecture

`echo-api` follows a flat, single-controller Spring MVC architecture — deliberate minimalism for a scaffold/reference
project.

```
HTTP Client
     │
     ▼
EchoApiController   ← @RestController mapped to {"", "/{*path}"}
     │               handles GET, PUT, PATCH, POST, DELETE
     │               builds RequestDetails record from HttpServletRequest
     ▼
JSON Response        ← serialized RequestDetails (method, url, path, headers, body, query, …)
```

**On startup**, `EchoApiApplication` listens for `ContextRefreshedEvent` to print an ASCII art banner (via Banana) and
JVM memory diagnostics.

There is no database, no external dependencies, and no required environment variables for local development.

---

## Getting Started

### Quick Start (Docker)

```bash
# Pull and run the latest pre-built native image
docker run --rm -p 8080:8080 ghcr.io/ranma2913/echo-api:latest
```

The API is available at `http://localhost:8080`.

### Local Development

For local setup, build commands, testing, coding standards, and troubleshooting, see **[DEVELOPMENT.md](DEVELOPMENT.md)
**.

---

## API Reference

All endpoints share the same wildcard mapping (`""` and `/{*path}`), so any path works.

### Response Schema

Every response returns a `RequestDetails` JSON object:

| Field         | Type              | Description                               |
|---------------|-------------------|-------------------------------------------|
| `method`      | `string`          | HTTP method (GET, POST, etc.)             |
| `url`         | `string`          | Full URL including query string           |
| `path`        | `string`          | Path segment only (no query string)       |
| `originalUrl` | `string`          | Request URI with query string             |
| `headers`     | `object`          | All request headers as key-value pairs    |
| `body`        | `object` / `null` | Parsed request body (null for GET/DELETE) |
| `query`       | `object`          | Query parameters as key-value pairs       |
| `params`      | `object`          | URI template path variables               |
| `ip`          | `string`          | Client remote address                     |
| `protocol`    | `string`          | `http` or `https`                         |
| `secure`      | `boolean`         | Whether the connection is HTTPS           |
| `host`        | `string`          | `Host` header value                       |
| `hostname`    | `string`          | Server name                               |

### Endpoints

| Method   | Path       | Status | Body Required |
|----------|------------|--------|---------------|
| `GET`    | `/{*path}` | 200    | No            |
| `POST`   | `/{*path}` | 201    | Yes           |
| `PUT`    | `/{*path}` | 200    | Yes           |
| `PATCH`  | `/{*path}` | 200    | Yes           |
| `DELETE` | `/{*path}` | 200    | No            |

### Example

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
  "headers": { "content-type": "application/json", "host": "localhost:8080" },
  "body": { "hello": "world" },
  "query": {},
  "params": {},
  "ip": "127.0.0.1",
  "protocol": "http",
  "secure": false,
  "host": "localhost:8080",
  "hostname": "localhost"
}
```

---

## CI/CD Pipelines

| Workflow           | Trigger                                | What it does                                                                                            |
|--------------------|----------------------------------------|---------------------------------------------------------------------------------------------------------|
| `ci.yaml`          | push / PR / `workflow_dispatch`        | Runs `mvnw verify`, executes Spock tests, posts JaCoCo PR comment, runs CodeQL security analysis        |
| `release.yaml`     | push to `master` / `workflow_dispatch` | Builds & pushes GraalVM native Docker image to GitHub Container Registry (`ghcr.io/ranma2913/echo-api`) |
| `semver-bump.yaml` | PR to non-master / `workflow_dispatch` | Bumps patch/minor/major version in `pom.xml`, commits, and tags                                         |

### Required GitHub Secrets / Variables

| Name           | Type     | Description                                                          |
|----------------|----------|----------------------------------------------------------------------|
| `GITHUB_TOKEN` | Built-in | Automatically provided; used to push to `ghcr.io` and comment on PRs |

---

## Contributing

1. Fork the repository and create a feature branch
2. Ensure `./mvnw test` passes with no JaCoCo violations before opening a PR
3. Follow [Conventional Commits](https://www.conventionalcommits.org/) with emoji for commit messages (see
   `.github/git-commit-instructions.md`)
4. PR title format: `[component] Brief description` — e.g., `[controller] Add HEAD method support`
5. CodeQL security analysis runs automatically — address any `security-extended` findings before merging
6. All new classes must be covered by at least one Spock spec (JaCoCo enforces 0 missed classes)

For coding standards, build instructions, and troubleshooting, see **[DEVELOPMENT.md](DEVELOPMENT.md)**.

---

## Security

- No authentication by default — add Spring Security before exposing beyond local/test environments
- No hardcoded secrets anywhere — use environment variables or GitHub Actions secrets
- CodeQL `security-extended,security-and-quality` queries run on every push and PR
- Full OWASP Top 10 guidance in `.github/instructions/security-and-owasp.instructions.md`

---

## License

See [LICENSE](LICENSE) for details.
