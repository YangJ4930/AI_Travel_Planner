# AI Travel Planner — Docker & CI Guide

## Quick Start
- Prereqs: Docker (20+), Docker Compose v2, optional Node 18+.
- Build images: `docker compose build`
- Run stack: `docker compose up -d`
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080

## Services
- `frontend`: Vue 3 + Vite, served by Nginx in the container.
- `backend`: Spring Boot (Java 17), uses MySQL and Redis.
- `database`: MySQL 8.0 with init script `backend/sql/create.sql`.
- `redis`: Redis 6-alpine.
- `nginx`: Reverse proxy (production only via profiles).

## Configuration
- Create `.env` at repo root (see `.env.example`). Key entries:
  - DB: `DB_USERNAME`, `DB_PASSWORD`, `DB_HOST`, `DB_PORT=3306`, `DB_NAME`
  - Redis: `REDIS_HOST`, `REDIS_PORT`
  - Third-party keys: Amap/Xunfei/Alibaba Cloud (backend-only)
- Frontend base URL at build time: `VITE_API_BASE_URL` defaults to `http://backend:8080/api` in compose.
- Runtime override: use app Settings page to set `API Base` and `X-Api-Key` stored in `localStorage`.

## GitHub Actions → ACR
- Workflow: `.github/workflows/docker-build.yml` builds and pushes on `main` or manual trigger.
- Required secrets (GitHub repo → Settings → Secrets and variables → Actions):
  - `ACR_REGISTRY` (e.g. `registry.cn-hangzhou.aliyuncs.com`)
  - `ACR_USERNAME` / `ACR_PASSWORD`
  - `ACR_NAMESPACE` (your namespace/project)
- Images:
  - Backend: `${ACR_REGISTRY}/${ACR_NAMESPACE}/ai-travel-backend:latest|${{ github.sha }}`
  - Frontend: `${ACR_REGISTRY}/${ACR_NAMESPACE}/ai-travel-frontend:latest|${{ github.sha }}`

## Release & Export
- Save images:
  - `docker save -o ai-travel-backend.tar ${ACR_REGISTRY}/${ACR_NAMESPACE}/ai-travel-backend:latest`
  - `docker save -o ai-travel-frontend.tar ${ACR_REGISTRY}/${ACR_NAMESPACE}/ai-travel-frontend:latest`
- Upload tars to your release assets and share pull command:
  - `docker load -i ai-travel-backend.tar`
  - `docker load -i ai-travel-frontend.tar`

## Temporary Evaluation Key
- If needed, publish a temporary `X-Api-Key` in README with validity and limits.
- Recommended: generate server-side, store as env, rate-limit and expire.
- Do NOT commit real secrets; use placeholders and rotate frequently.