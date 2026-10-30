# Calculator REST API — CI/CD Mini Project

Harsh Gupta (IMT2023121)

A production-style Calculator REST API built with Spring Boot and Maven, delivered through GitHub, Jenkins, Docker, and Docker Compose. This repository fulfils the CSE 816 Software Production Engineering mini-project scope.

## Architecture

```text
Developer push → GitHub webhook → Jenkins
                                  ├─ Checkout → Build → Test + JUnit report
                                  ├─ Docker build → Docker Hub push
                                  └─ docker compose up -d → Nginx → Calculator API
                                                               └─ /actuator/health
```

Nginx is the public-facing reverse proxy. The Calculator API remains on the internal `calculator-network`; only Nginx publishes a host port.

## API

All operations accept numeric query parameters `a` and `b` and return JSON.

| Operation | Request                          |
| --------- | -------------------------------- |
| Add       | `GET /api/v1/add?a=2&b=3`      |
| Subtract  | `GET /api/v1/subtract?a=2&b=3` |
| Multiply  | `GET /api/v1/multiply?a=2&b=3` |
| Divide    | `GET /api/v1/divide?a=10&b=4`  |
| Health    | `GET /actuator/health`         |

Example response:

```json
{"operation":"add","a":2,"b":3,"result":5}
```

Missing/non-numeric parameters and division by zero return HTTP 400 with a structured JSON error body.

## Run locally

Prerequisites: Java 11+, Maven 3.8+, and Docker Compose v2 for the container deployment.

```bash
mvn clean test
mvn spring-boot:run
curl 'http://localhost:8080/api/v1/divide?a=10&b=4'
curl 'http://localhost:8080/actuator/health'
```

To build and deploy the complete Compose stack (API + Nginx):

```bash
docker compose up --build -d
docker compose ps
curl 'http://localhost:8080/api/v1/multiply?a=6&b=7'
docker compose down
```

To run the API image directly:

```bash
docker build -t calculator-api:local .
docker run --rm -p 8080:8080 calculator-api:local
```

## Jenkins setup

1. Create a **Pipeline** job that uses “Pipeline script from SCM” and points to this repository’s `Jenkinsfile`.
2. Install/configure Pipeline, Git, GitHub, JUnit, Docker Pipeline, Email Extension, and Workspace Cleanup plugins. Ensure the Jenkins agent can run `mvn`, `docker`, `docker compose`, and `curl`.
3. In **Manage Jenkins → Credentials**, create a global Username with password credential named exactly `dockerhub-credentials`. Use Docker Hub username as the username and a Docker Hub access token as the password.
4. Replace `YOUR_DOCKERHUB_USERNAME` and `YOUR_STUDENT_EMAIL@example.com` in `Jenkinsfile` before committing. Configure SMTP and the Extended E-mail Notification plugin in **Manage Jenkins → System**.
5. If Jenkins runs locally, expose it with a temporary public HTTPS tunnel, for example `cloudflared tunnel --url http://localhost:8081`. Set the Jenkins URL to the generated tunnel URL, then add a GitHub webhook at `<tunnel-url>/github-webhook/` with content type `application/json` and push events enabled. Keep the tunnel running while testing; the `githubPush()` trigger starts the pipeline automatically.
6. Create the public Docker Hub repository named `calculator-api`. The pipeline tags images with Jenkins’ build number and `latest`.

The Deploy stage is idempotent: Compose reconciles the existing stack, removes orphan containers, and waits for the proxied health endpoint to report `UP`.

## Evidence to capture

See [`docs/submission-checklist.md`](docs/submission-checklist.md). [`docs/mini-project-report.md`](docs/mini-project-report.md) is a concise report draft: replace placeholders, export it as PDF, and add your own screenshots.

Never commit `.env`, Docker Hub tokens, private keys, or Jenkins credentials. Use a feature branch and pull request/merge before submission, keeping a meaningful progressive commit history.
