# ✅ Production Launch Checklist

Before promoting any environment to the public `production` domain, verify all items below.

## Security
- [ ] **HTTPS**: All endpoints (Backend and Frontend) are secured via TLS 1.2+ (handled natively by Vercel/Render).
- [ ] **CORS**: Backend `WebSecurityConfig` explicitly whitelists the Vercel production domain (NO `*` wildcards allowed).
- [ ] **Secrets**: No API keys, JWT secrets, or DB passwords are hardcoded. All rely on Vault or Cloud Environment Variables.
- [ ] **JWT**: Token expiration is strictly enforced (Access: 15m, Refresh: 7d).

## Database & Migrations
- [ ] **Flyway**: `V1__init.sql` and subsequent migrations ran successfully on the production RDS instance.
- [ ] **Backups**: Automated daily snapshot backups are enabled on the cloud database provider.
- [ ] **Indexes**: High-traffic query columns (e.g., `doctor_id`, `patient_id` on the `appointments` table) are properly indexed.

## Backend (Spring Boot & Docker)
- [ ] **Profiles**: Application is launched with the `prod` Spring profile (`-Dspring.profiles.active=prod`).
- [ ] **Logging**: Hibernate SQL query logging (`show-sql=true`) is disabled to prevent log flooding and sensitive data leaks.
- [ ] **Docker**: Running the distroless/JRE Alpine multi-stage build image to minimize attack surface.
- [ ] **Health Checks**: `/actuator/health` endpoint is exposed and monitored by the deployment platform.

## Frontend (React & Vite)
- [ ] **Environment Variables**: `VITE_API_URL` is set to the production backend URL in Vercel settings.
- [ ] **Bundle Splitting**: `manualChunks` successfully split vendors. No single JS chunk exceeds 800kb.
- [ ] **Console Logs**: Vite `esbuild` is configured to `drop: ['console', 'debugger']` in production.
- [ ] **Routing**: Vercel `vercel.json` SPA rewrite rules are active, preventing 404s on page refresh.

## CI/CD & Monitoring
- [ ] **GitHub Actions**: The `main` branch is protected; PRs require successful JUnit and Vitest runs before merging.
- [ ] **Monitoring**: UptimeRobot (or similar) is configured to ping the frontend URL and backend `/actuator/health` every 5 minutes.
