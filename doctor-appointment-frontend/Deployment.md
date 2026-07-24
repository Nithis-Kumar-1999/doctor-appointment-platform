# Deployment Guide

## Prerequisites
- Node.js v18+
- A deployed Spring Boot backend with CORS configured for your frontend URL.

---

## 🚀 Frontend — Vercel (Recommended)

### Option A: Vercel GUI (Easiest)

1. Push your code to a GitHub repository.
2. Log in to [vercel.com](https://vercel.com) → New Project → Import your repository.
3. Vercel will auto-detect **Vite**. Override if needed:
   - **Framework Preset**: Vite
   - **Build Command**: `npm run build`
   - **Output Directory**: `dist`
   - **Install Command**: `npm ci`
4. Under **Environment Variables**, add:
   ```
   VITE_API_BASE_URL = https://your-backend.onrender.com
   ```
5. Click **Deploy**.
6. The `vercel.json` in the root handles:
   - SPA routing (`/* → /index.html`)
   - Security headers (`X-Frame-Options`, `X-Content-Type-Options`)
   - Asset caching (`public, max-age=31536000, immutable`)

### Option B: Vercel CLI

```bash
npm install -g vercel
vercel login
vercel --prod
```

---

## ☁️ Backend — Render

The backend is a Spring Boot JAR deployed on [Render](https://render.com).

### Environment Variables to set on Render:

```properties
# Database
DB_URL=jdbc:mysql://your-db-host:3306/appointment_db
DB_USERNAME=your_db_user
DB_PASSWORD=your_db_password

# JWT
JWT_SECRET=your-256-bit-secret-key-minimum-32-characters
JWT_EXPIRATION_MS=86400000

# Server
PORT=8080
```

### CORS Configuration

Ensure your Spring Boot `application.properties` allows the frontend URL:

```properties
# In CorsConfig.java — update allowedOrigins to include:
# https://your-app.vercel.app
```

### Verify Deployment
- Backend Health: `GET https://your-backend.onrender.com/actuator/health`
- Swagger Docs: `https://your-backend.onrender.com/swagger-ui/index.html`

---

## 🔍 Production Verification Checklist

- [ ] Frontend loads at production URL without console errors.
- [ ] Login and Registration work against the production backend.
- [ ] JWT is correctly stored and attached to API requests.
- [ ] Protected routes redirect unauthorized users to `/login`.
- [ ] Doctor search returns real data.
- [ ] Appointment booking creates records in the database.
- [ ] Responsive layout is verified on mobile (375px) and desktop (1280px).
