# Deploying 01Blog on Northflank (free Sandbox plan)

The deployment uses exactly what the Sandbox plan includes: **1 PostgreSQL addon + 2 services**.

```
Browser ──HTTPS──▶ frontend (nginx, PUBLIC :8080)
                      ├── /            → Angular build (SPA fallback to index.html)
                      ├── /api/*  ─┐
                      └── /auth/* ─┴──▶ backend (Spring Boot, PRIVATE :8080) ──▶ PostgreSQL addon
```

The browser only talks to the frontend's public URL. nginx forwards `/api/` and `/auth/` to the
backend over Northflank's private network, so there is no CORS and the backend is never exposed.

---

## 0. Before you start

- The code must be pushed to GitHub (`main` branch).
- Sign in at <https://app.northflank.com> and connect your GitHub account:
  **Account settings → Git integrations → GitHub → Link** and give it access to the `01Blog` repo.
- Generate a JWT secret on your machine (keep it private, never commit it):

  ```bash
  openssl rand -base64 64 | tr -d '\n'; echo
  ```

## 1. Create the project

1. **Create new → Project**.
2. Name: `01blog`.
3. Region: **Europe – West**.
4. Click **Create project**.

## 2. Create the PostgreSQL addon

1. Inside the project: **Create new → Addon**.
2. Type: **PostgreSQL**. Pick the latest version offered (15 or newer is fine).
3. Name: `blog-db`.
4. Leave **Deploy with TLS** as default and do **not** enable *Publicly accessible*.
5. Resources: keep the free/smallest plan.
6. Click **Create addon** and wait until its status is **Running**.
7. Open the addon → **Connection details**. You will copy these values in the next step:
   - `HOST` (an internal host name)
   - `PORT` (usually `5432`)
   - `DATABASE`
   - `USERNAME`
   - `PASSWORD`

## 3. Create the backend service (create it BEFORE the frontend)

1. **Create new → Service → Combined service** (build + deploy from Git).
2. Name: `backend` (this name becomes the internal host name used by the frontend).
3. **Repository**: select `01Blog`, branch `main`.
4. **Build options**: choose **Dockerfile**.
   - Build context: `/backend`
   - Dockerfile location: `/backend/Dockerfile`
5. **Networking → Ports**: add port `8080`, protocol `HTTP`, and leave **Publicly expose this port
   to the internet** **OFF** (private only).
6. **Environment variables** (Runtime variables). Add:

   | Variable | Value | Where it comes from |
   |---|---|---|
   | `SPRING_DATASOURCE_URL` | `jdbc:postgresql://<HOST>:<PORT>/<DATABASE>` | addon → Connection details (`HOST`, `PORT`, `DATABASE`). If the connection is refused because TLS is required, append `?sslmode=require`. |
   | `SPRING_DATASOURCE_USERNAME` | `<USERNAME>` | addon → Connection details |
   | `SPRING_DATASOURCE_PASSWORD` | `<PASSWORD>` | addon → Connection details |
   | `JWT_SECRET` | output of `openssl rand -base64 64` | step 0 |
   | `APP_BASE_URL` | the frontend's public URL, e.g. `https://p01--frontend--xxxx.code.run` (no trailing slash) | fill in after step 4, then restart the backend |
   | `PORT` | `8080` | optional, this is the default |
   | `JAVA_OPTS` | `-Xmx256m -Xss512k -XX:MaxMetaspaceSize=150m -XX:+UseSerialGC -XX:TieredStopAtLevel=1` | optional, already the image default; lower `-Xmx` if your plan has less than 512 MB |

   `APP_CORS_ORIGINS` is not needed (everything is same-origin through nginx).

   > Tip: instead of copying addon values by hand you can use **Secret groups → Link addon**, which
   > injects the addon's connection variables; you still need `SPRING_DATASOURCE_*` names, so the
   > manual table above is the simplest.

7. **Resources**: choose the free Sandbox compute plan with the most memory available.
8. **Health checks**: leave none, or add an HTTP check with a generous initial delay (≥ 120 s) – the JVM
   starts slowly on a small CPU share.
9. Click **Create service**. The first build takes several minutes (Maven downloads dependencies).

## 4. Create the frontend service

1. **Create new → Service → Combined service**.
2. Name: `frontend`.
3. Repository `01Blog`, branch `main`.
4. **Build options → Dockerfile**:
   - Build context: `/frontend`
   - Dockerfile location: `/frontend/Dockerfile`
5. **Networking → Ports**: port `8080`, protocol `HTTP`, **Publicly expose this port to the internet: ON**.
6. **Environment variables**:

   | Variable | Value |
   |---|---|
   | `BACKEND_URL` | `http://backend:8080` – the backend's internal URL (shown on the backend service page under **Ports / Private endpoints**; if you named the service differently use that name) |

   No trailing slash. nginx passes the original path through, so `/api/posts` reaches the backend
   as `/api/posts`.

7. Click **Create service** and wait for the build and deployment.
8. Open the frontend service → **Ports & DNS** and copy the public URL (`https://...code.run`).

## 5. Set APP_BASE_URL and restart the backend

1. Backend service → **Environment** → set `APP_BASE_URL` to the frontend public URL from step 4.8.
2. **Save & redeploy** (or *Restart*).

`APP_BASE_URL` is used to build the image/video/avatar links stored in the database, so set it
before registering users or uploading files. Links created with a wrong value stay wrong.

## 6. Test

1. Open the frontend public URL.
2. Register a user (the username `admin` gets the ADMIN role), log in, create a post with an image.
3. Right-click the image → open in new tab: the URL should be `https://<frontend-url>/api/posts/images/<uuid>.png`.

## 7. Logs and troubleshooting

- **Build logs**: service → **Builds** → click the build → log output.
- **Runtime logs**: service → **Logs** (live tail; filter by container).
- **Container status / restarts / OOM kills**: service → **Containers** and **Metrics**.

| Symptom | Likely cause |
|---|---|
| frontend container crash-loops with `host not found in upstream "backend"` | `BACKEND_URL` host is wrong, or the backend service does not exist yet. Create the backend first / fix the name, then restart the frontend. |
| `502 Bad Gateway` on `/api/...` | backend still starting or crashed – check backend runtime logs. |
| backend log `Connection to <host> refused` / `password authentication failed` | wrong `SPRING_DATASOURCE_*` values; recheck the addon connection details. `SSL required` → add `?sslmode=require`. |
| backend killed with exit code 137 | out of memory – lower `-Xmx` in `JAVA_OPTS` or pick a plan with more RAM. |
| images show `localhost:8080` URLs | `APP_BASE_URL` was not set when they were uploaded. |

## Local development (unchanged)

- Without Docker: run PostgreSQL locally, `cd backend && mvn spring-boot:run`, `cd frontend && npm start`
  → <http://localhost:4200> (the app detects `localhost` and calls `http://localhost:8080`).
- With Docker: `docker compose up --build` → frontend (nginx) on <http://localhost:4200>,
  backend on <http://localhost:8080>, PostgreSQL on `localhost:5432`.

## Limitations

- **Uploaded files are ephemeral.** Images, videos and avatars are written to the backend
  container's disk (`/app/uploads`). Every redeploy/restart on Northflank starts from a fresh
  container, so uploads are lost (the database rows still point to them → broken images).
  Simplest free fixes, in order of effort:
  1. **Store uploads in PostgreSQL** (a `bytea` column / small `media` table served by the same
     `/api/posts/images/{name}` endpoints). Uses the addon you already have; no new account. Mind the
     addon's small storage quota.
  2. **Use a free object store** such as Cloudinary (free tier) or Cloudflare R2 (10 GB free) and
     save the returned URL – the frontend already works with absolute image URLs.
  Northflank persistent volumes are not part of the free Sandbox plan.
- Free Sandbox services have a small CPU share: the backend can take 1–2 minutes to start.
- The first registered user named `admin` becomes administrator – register it yourself right after
  deploying.
