# 🚀 Production Deployment & Portfolio Checklist

This document contains the exact manual steps required to deploy the application and the screenshots you must capture to prove it works. **Do not skip steps.**

## ☁️ PART 1: Backend Deployment (Render)

1. **Database Provisioning**:
   - Create a new PostgreSQL or MySQL instance on Render (or Railway).
   - Copy the connection string.
2. **Web Service Creation**:
   - Create a New Web Service connected to your GitHub repo.
   - **Build Command**: `./mvnw clean package -DskipTests`
   - **Start Command**: `java -jar target/app.jar`
3. **Environment Variables Configuration**:
   - You MUST manually inject these into the Render dashboard:
     - `SPRING_PROFILES_ACTIVE`: `prod`
     - `SPRING_DATASOURCE_URL`: `[Your Database Connection String]`
     - `SPRING_DATASOURCE_USERNAME`: `[Your DB Username]`
     - `SPRING_DATASOURCE_PASSWORD`: `[Your DB Password]`
     - `JWT_SECRET`: `[Generate a secure 256-bit random string]`
     - `APP_CORS_ALLOWED_ORIGINS`: `https://[your-vercel-app].vercel.app` *(Critical: Do not use trailing slashes)*

## ☁️ PART 2: Frontend Deployment (Vercel)

1. **Vercel Setup**:
   - Import the frontend repository into Vercel.
   - Vercel will automatically detect `Vite` and run `npm run build`.
2. **Environment Variables Configuration**:
   - You MUST manually inject this into the Vercel dashboard BEFORE deploying:
     - `VITE_API_URL`: `https://[your-render-app].onrender.com` *(Critical: Do not use trailing slashes)*
3. **Security & Routing**:
   - Ensure `vercel.json` exists in the root of your project. This handles SPA fallback (`/(.*) -> /index.html`) so refreshing the page doesn't throw a 404.

## ✅ PART 3: Post-Deployment Verification

Physically test the live URLs to verify the systems are communicating.

### Backend Checklist
- [ ] Navigating to `https://[render-url]/actuator/health` returns `{"status":"UP"}`.
- [ ] Navigating to `https://[render-url]/swagger-ui.html` loads the API docs.
- [ ] Swagger successfully executes `POST /api/v1/auth/login`.

### Frontend Checklist
- [ ] You can register a new Patient.
- [ ] Logging in redirects to the Dashboard.
- [ ] Hitting F5 (Refresh) on the Dashboard does not throw a 404.
- [ ] Inspecting Chrome DevTools > Network tab shows the API requests hitting the Render URL, not `localhost`.
- [ ] Shrinking the browser window toggles the Mobile Hamburger menu.

---

## 📸 PART 4: Real Screenshot Capture Guide

Create a folder named `docs/screenshots/`. Use a tool like [CleanShot X](https://cleanshot.com) or [Screely](https://screely.com) to capture the following exact views. 

*Note: Populate the database with realistic mock data (e.g., "Dr. Sarah Jenkins") before capturing.*

1. **`landing.png`**: The hero banner on the homepage. Use this at the top of your `README.md`.
2. **`login.png`**: The Auth form showing an actively typed email address.
3. **`register.png`**: The registration form showing a **validation error** in red to prove Zod works.
4. **`doctor-dashboard.png`**: The Doctor view showing the statistics cards and today's appointments table. Use in the README side-by-side with Patient.
5. **`patient-dashboard.png`**: The Patient view showing health summaries and upcoming visits.
6. **`booking.gif`**: A short 5-second screen recording of selecting a date, picking a time slot, and clicking Confirm. This is the **most important asset** for your LinkedIn post.
7. **`appointment-details.png`**: The Timeline Stepper showing "Scheduled -> Confirmed".
8. **`swagger.png`**: The Swagger UI with the `/api/v1/doctors` GET endpoint expanded to show the schema.
9. **`github-actions.png`**: The GitHub Actions `backend-ci.yml` workflow graph showing green checkmarks. Use in your resume to prove CI/CD skills.
10. **`mobile.png`**: Shrink the browser to an iPhone width and capture the Patient dashboard with the sidebar hidden.
