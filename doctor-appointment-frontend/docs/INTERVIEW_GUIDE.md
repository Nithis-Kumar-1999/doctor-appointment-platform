# 🎙️ Technical Interview Guide

*This document contains "Elevator Pitches" designed to help you confidently explain the architectural decisions behind this project during technical interviews.*

---

## 1. "Walk me through the architecture of your application." (2 Minutes)
"This is an N-Tier, Full-Stack SaaS application. The frontend is a Single Page Application (SPA) built with React 19 and TypeScript, bundled by Vite for performance. It communicates via REST to a Java Spring Boot backend. The backend strictly follows Clean Architecture: controllers handle HTTP parsing, services contain pure business logic, and repositories interface with a MySQL database. For security, I implemented stateless JWT authentication. The entire database is version-controlled via Flyway, and the backend is containerized with Docker. Both sides are protected by CI/CD pipelines running JUnit and Vitest tests before deploying to Render and Vercel."

---

## 2. "How did you implement Authentication?" (Explain JWT & Refresh Tokens)
"I chose a stateless JWT approach to ensure horizontal scalability. When a user logs in, Spring Security issues two tokens: a short-lived Access Token (15 mins) and a long-lived Refresh Token (7 days). The frontend stores the Access Token in memory to protect against Cross-Site Request Forgery (CSRF). When an API request returns a 401 Unauthorized, an Axios interceptor catches the error, automatically sends the Refresh Token to get a new Access Token, and silently retries the original request. The user never notices."

---

## 3. "How did you manage state in React?"
"I completely avoided global stores like Redux. Instead, I separated 'Server State' from 'Client State'. 
For Server State (data from the database), I used **TanStack Query**. It handles caching, deduplication, and loading states out of the box, drastically reducing boilerplate. For Client State (like Light/Dark theme or the currently logged-in user context), I used React's native **Context API**. For complex form states (like the multi-step booking wizard), I used **React Hook Form** paired with **Zod** to prevent unnecessary re-renders while ensuring strict type-safe validation."

---

## 4. "How did you optimize Frontend Performance?"
"I targeted Google's Core Web Vitals. To fix the Largest Contentful Paint (LCP), I pre-connected Google Fonts and added `display=swap`. To optimize the JavaScript bundle size, I configured Vite to use Rollup's `manualChunks`, splitting React and Material UI into separate cacheable vendor files. For routing, I used `React.lazy()` and `Suspense`, so if a patient logs in, their browser doesn't download the Javascript for the Doctor's dashboard until they actually need it."

---

## 5. "Why did you use Flyway and Docker?"
"**Flyway** solves the 'it works on my machine' database problem. Instead of relying on Hibernate's `ddl-auto=update` (which is dangerous in production), every schema change is written as an immutable SQL script. Flyway ensures the database exactly matches the code's expectations. 
**Docker** guarantees runtime consistency. I used a multi-stage `Dockerfile`. Stage 1 compiles the Java code using Maven, and Stage 2 creates a tiny JRE-only image to run the `.jar`. This keeps the production container lightweight and secure."

---

## 6. "How did you handle N+1 Queries in Spring Boot?"
"In JPA, if you query a list of Appointments and then call `appointment.getDoctor().getName()`, Hibernate will execute 1 query to get the appointments, and N subsequent queries to fetch the doctors. I mitigated this by using `@EntityGraph(attributePaths = {"doctor", "patient"})` on my Repository methods. This forces Hibernate to perform a single `LEFT OUTER JOIN` at the database level, bringing the data back in one highly efficient query."
