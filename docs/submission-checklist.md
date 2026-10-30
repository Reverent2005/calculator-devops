# Submission and Demo Checklist

## Before committing

- [ ] Replace the Docker Hub username and student email placeholders in `Jenkinsfile`.
- [ ] Confirm the Docker Hub credential ID is `dockerhub-credentials` and the public Docker Hub repository is named `calculator-api`.
- [ ] Set the actual GitHub and Docker Hub links in the report.
- [ ] Run `mvn clean test` successfully.
- [ ] Run `docker compose up --build -d`, verify both services are healthy, then run `docker compose down`.
- [ ] Do not commit `.env`, tokens, passwords, or generated `target/` files.

## Git marks

- [ ] Create the GitHub repository and push an initial project commit.
- [ ] Create a feature branch (for example, `feature/error-handling`), make a meaningful change, open a pull request, and merge it.
- [ ] Make progressive, descriptive commits—not one final commit.

## Jenkins evidence

- [ ] Screenshot the complete green pipeline showing Checkout, Build, Test, Docker Build, Push, and Deploy.
- [ ] Screenshot the published JUnit test report.
- [ ] Screenshot the success email and GitHub’s successful webhook delivery.

## Deployment evidence

- [ ] Screenshot `docker compose ps` or `docker ps` showing calculator-api and nginx running.
- [ ] Screenshot a successful API response, a divide-by-zero `400` JSON response, and `/actuator/health` returning `UP`.

## Live demo sequence

1. Show the GitHub repository, branches/PR, and Docker Hub image tags.
2. Make and push a small safe change.
3. Show the GitHub webhook triggering Jenkins without manual “Build Now”.
4. Show the green stages and email notification.
5. Show Compose services and call the endpoint through Nginx.
