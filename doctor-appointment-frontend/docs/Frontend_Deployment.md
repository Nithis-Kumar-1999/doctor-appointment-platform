# Frontend Deployment & Troubleshooting Guide

## Deployment Instructions (Vercel)

1. **Local Build & Test**
   Before deploying, ensure the application builds successfully locally.
   ```bash
   npm run build
   npm run preview
   ```

2. **Connecting to Vercel**
   - Push your code to a GitHub repository.
   - Log into Vercel and click **Add New Project**.
   - Import your GitHub repository.

3. **Configure Environment Variables**
   During the Vercel setup, add the following to the **Environment Variables** section:
   - `VITE_API_URL`: Your production backend URL (e.g., `https://api.clinic.com`).

4. **Deploy**
   - Click **Deploy**. Vercel will automatically detect Vite, run `npm run build`, and serve the `dist/` directory.

5. **Rollback**
   - Vercel automatically creates immutable deployments for every commit.
   - To rollback, navigate to the **Deployments** tab in the Vercel dashboard, click the vertical dots next to a previous successful deployment, and select **Promote to Production**.

---

## Troubleshooting Guide

### 1. 404 Error After Refreshing a Page
**Symptom**: Navigating to `/patient/dashboard` works, but refreshing the page returns a 404.
**Fix**: This occurs in Single Page Applications (SPAs) because the server tries to find a physical folder named `patient/dashboard`. Our `vercel.json` fixes this by implementing a rewrite rule that routes all traffic `/(.*)` back to `/index.html`.

### 2. Missing Environment Variables
**Symptom**: The app loads, but API requests fail or try to hit `localhost:8080`.
**Fix**: Vercel does not read `.env.production` files pushed to Git automatically. You must manually add `VITE_API_URL` to the Environment Variables section in the Vercel Project Settings. Note that variables must start with `VITE_` to be exposed to the React code.

### 3. CORS Issues
**Symptom**: Console shows `Cross-Origin Request Blocked`.
**Fix**: Ensure your Spring Boot backend's `@CrossOrigin` configuration or `WebSecurityConfig` allows origins matching your new Vercel domain (e.g., `https://your-app.vercel.app`).

### 4. API Timeout
**Symptom**: API requests hang or fail with a 504 Gateway Timeout.
**Fix**: This usually indicates the Render/Railway backend is "sleeping" (if on a free tier) or the Vercel frontend is using `http://` instead of `https://` to communicate with the backend, triggering mixed-content blocks. Ensure `VITE_API_URL` begins with `https://`.
