# Live demo and restart runbook

This guide starts the existing local Jenkins and Calculator deployment and reconnects the GitHub webhook. It assumes Docker Desktop and the project have already been configured.

## What stays running

- Jenkins is the Docker container named `jenkins`, exposed at `http://localhost:8081`. Its Jenkins home is stored in the persistent Docker volume `jenkins_home`, so jobs and configuration survive container restarts.
- The Calculator API and Nginx run through this repository's Compose file. Nginx is exposed at `http://localhost:8080`.
- `cloudflared` is a separate process that provides a public URL for GitHub to reach Jenkins.

## Start everything

Open PowerShell and go to the project folder:

```powershell
cd C:\Codes\calculator-devops
```

Start Docker Desktop and wait until Docker is ready. Start the existing Jenkins container and the app:

```powershell
docker start jenkins
docker compose up --build -d
docker compose ps
```

Wait until both Compose services show healthy. Verify the app through Nginx:

```powershell
curl.exe http://localhost:8080/actuator/health
curl.exe "http://localhost:8080/api/v1/add?a=2&b=3"
```

Health should report `UP`; the calculation result should be `5`. Open Jenkins at `http://localhost:8081` and sign in. The configured job is `calculator-devops-cicd` and builds the `main` branch.

If Jenkins does not open, check the container:

```powershell
docker ps -a --filter "name=jenkins"
docker logs --tail 100 jenkins
```

If the app is not healthy, inspect its logs:

```powershell
docker compose logs --tail 100
```

## Start the GitHub webhook tunnel

In a **separate PowerShell window**, start the Quick Tunnel to Jenkins:

```powershell
cloudflared tunnel --url http://localhost:8081
```

Leave this window open. Copy the `https://....trycloudflare.com` URL printed in the output.

If the URL differs from the one currently configured in GitHub:

1. In GitHub, open the repository's **Settings → Webhooks → Jenkins webhook → Edit**.
2. Set **Payload URL** to `https://YOUR-CURRENT-URL/github-webhook/`, replacing the example with the copied URL.
3. Keep content type as `application/json`, select **Just the push event**, and save.
4. In Jenkins, open **Manage Jenkins → System → Jenkins Location** and set **Jenkins URL** to `https://YOUR-CURRENT-URL/`. Save.

Keep the tunnel, Docker Desktop, Jenkins, and the app running for the whole demo.

## Trigger and show the pipeline

1. Push a small change to `main` (or use **Redeliver** on a recent GitHub push delivery).
2. In GitHub, open the webhook's **Recent Deliveries**. A `200` response confirms Jenkins received the request.
3. In Jenkins, open **`calculator-devops-cicd`** and click the new build to show its progress and stages.
4. Recheck the deployed API:

   ```powershell
   curl.exe "http://localhost:8080/api/v1/divide?a=10&b=4"
   curl.exe -i "http://localhost:8080/api/v1/divide?a=1&b=0"
   curl.exe http://localhost:8080/actuator/health
   ```

   Division by zero should return HTTP 400; health should report `UP`.

## If the webhook does not start a build

- Confirm the GitHub delivery is a `push` to `main`; the Jenkins job is configured for `main`.
- Confirm the tunnel process is still running and the payload URL uses its current hostname and ends in `/github-webhook/`.
- A `200` response means Jenkins received the webhook, but does not guarantee the job started. Check the Jenkins job's build history and confirm the GitHub push trigger is enabled.
- If the tunnel process stopped or the laptop restarted, start a new tunnel and update the webhook URL. Quick Tunnel hostnames change when a new tunnel is created.

## Tunnel lifetime and cleanup

A Quick Tunnel stays available only while its `cloudflared` process is running and the laptop/network remains available. Cloudflare provides no uptime guarantee for Quick Tunnels; treat one as temporary and verify it shortly before presenting. Do not stop or restart it during the demo. If you do restart it, expect a new hostname and update the GitHub webhook (and Jenkins URL) as described above.

When finished, stop `cloudflared` with **Ctrl+C** in its PowerShell window. Stop the app containers if desired:

```powershell
cd C:\Codes\calculator-devops
docker compose down
```

Stopping the Jenkins container is optional; its persistent `jenkins_home` volume preserves its configuration and build history:

```powershell
docker stop jenkins
```
