# CSE 816 Mini-Project Report: Calculator REST API CI/CD Pipeline

**Student:** [Your name]  
**Roll number:** [Your roll number]  
**GitHub repository:** [repository URL]  
**Docker Hub image:** [image URL]

## 1. Overview and architecture

This project delivers a Calculator REST API using Java, Spring Boot, and Maven. Its complete path from a Git push to a running deployment is automated with Git/GitHub, Jenkins, Docker, and Docker Compose.

```text
Git feature branch / pull request
              │ merge and push
              ▼
       GitHub webhook
              ▼
            Jenkins
 Checkout → Build → Test → Docker Build → Push → Deploy
                                      │              │
                                      ▼              ▼
                                Docker Hub     Docker Compose
                                                     │
                                              Nginx → Calculator API
                                                        │
                                               Actuator health check
```

The API provides add, subtract, multiply, divide, and Actuator health endpoints. Successful operations return JSON. Invalid input and division by zero return a structured HTTP 400 response. Nginx is a second Compose service which proxies requests to the API and demonstrates orchestration and network isolation.

## 2. Pipeline stages

The declarative `Jenkinsfile` is stored in source control and a GitHub webhook invokes it after a push. Checkout fetches the triggering commit. Build uses `mvn clean package -DskipTests`; Test runs `mvn test` and publishes Surefire JUnit XML results, failing the pipeline on test failure.

Docker Build creates a multi-stage image tagged both with the Jenkins build number and `latest`. Push logs into Docker Hub through Jenkins-managed credentials and pushes both tags; no credential is kept in the repository. Deploy passes the immutable build-number tag to Compose, runs `docker compose up -d --remove-orphans`, then polls the public Actuator health endpoint. The deployment is safe to rerun because Compose reconciles the existing stack. Jenkins sends a success or failure email through the Email Extension plugin.

## 3. Container and deployment design

The Dockerfile uses Maven with JDK 17 only in the build stage. Its final image is a Temurin JRE image containing only the executable JAR. It runs under a dedicated non-root account and exposes port 8080. The build manifest is copied before source code to improve dependency-cache reuse, while `.dockerignore` excludes source-control, IDE, build, documentation, and local-secret files from the build context.

Docker Compose creates an isolated bridge network. The Calculator API has environment-driven configuration, a restart policy, and a health check that calls `/actuator/health`. Nginx waits for that health check, has a health check of its own, and is the only service exposing a host port. Thus the API is not directly accessible outside its Docker network.

## 4. Testing and challenges

JUnit 5 service tests cover all four arithmetic operations and division by zero. A Spring Boot MockMvc integration test calls the add endpoint and checks the divide-by-zero error body. Maven creates XML reports under `target/surefire-reports` for Jenkins to publish.

One challenge was deploying the exact image created by a Jenkins run without hard-coding an image tag. The pipeline solves this by exporting `CALCULATOR_IMAGE` with the Jenkins build number before Compose starts. Another challenge was secure Docker Hub authentication; Jenkins retrieves the account and token from a credential binding, supplies the token to `docker login --password-stdin`, and never logs or commits it.

## 5. Conclusion

The project demonstrates a repeatable CI/CD workflow: a GitHub push automatically builds, tests, containerizes, publishes, and deploys the Calculator service. This foundation can later be extended with Ansible, Kubernetes, and monitoring for the capstone.

> Add your own screenshots before exporting this document as a 2–3 page PDF: green pipeline, email notification, running containers, successful API response, health response, and HTTP 400 response.
