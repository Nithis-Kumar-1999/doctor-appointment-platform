<div align="center">

# 🏥 Doctor Appointment Platform

### A full-stack, production-ready healthcare scheduling application

[![React](https://img.shields.io/badge/React-19-%2361DAFB?style=flat-square&logo=react)](https://reactjs.org/)
[![TypeScript](https://img.shields.io/badge/TypeScript-6.0-blue?style=flat-square&logo=typescript)](https://www.typescriptlang.org/)
[![Vite](https://img.shields.io/badge/Vite-8.x-646CFF?style=flat-square&logo=vite)](https://vitejs.dev/)
[![Material UI](https://img.shields.io/badge/MUI-v9-007FFF?style=flat-square&logo=mui)](https://mui.com/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?style=flat-square&logo=springboot)](https://spring.io/projects/spring-boot)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=flat-square)](./LICENSE)

[**Live Demo**](https://your-live-demo-url.vercel.app) · [**Backend API**](https://doctor-backend-f7nj.onrender.com/swagger-ui/index.html) · [**Report Bug**](https://github.com/your-username/doctor-appointment-platform/issues)

</div>

---

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Screenshots](#screenshots)
- [Folder Structure](#folder-structure)
- [Installation](#installation)
- [Environment Variables](#environment-variables)
- [Deployment](#deployment)
- [API Documentation](#api-documentation)
- [Future Improvements](#future-improvements)
- [License](#license)

---

## 🌟 Overview

The **Doctor Appointment Platform** is a fully-featured, production-grade healthcare management system that seamlessly connects patients with medical professionals. Built with a feature-based modular architecture, it offers a clean, accessible, and responsive experience powered by modern React patterns.

The frontend integrates with a live Spring Boot REST API deployed on Render, secured with JWT authentication, and built with zero TypeScript errors and zero ESLint warnings.

---

## ✅ Features

### 🔐 Authentication & Security
- JWT-based stateless authentication
- Role-based access control (PATIENT / DOCTOR)
- Protected routes with automatic redirect-to-login
- Axios interceptor for automated token injection
- Global 401 handler — auto logout on expired sessions

### 👨‍⚕️ Doctor Module
- Professional profile creation and editing
- Weekly availability schedule management
- Appointment dashboard with sorting, filtering, and search
- Confirm / Complete / Cancel appointments with confirmation dialogs

### 🧑‍💼 Patient Module  
- Personal health profile creation and editing
- Advanced doctor search with specialty and city filters
- Smart appointment booking with dynamically generated time slots
- Personal appointment history with status filtering and cancellation

### 📅 Appointment System
- Interactive slot picker based on doctor's weekly schedule
- Real-time cache invalidation via React Query
- Appointment status lifecycle: `PENDING → CONFIRMED → COMPLETED`
- Cancellation with confirmation dialogs

---

## 💻 Tech Stack

| Layer | Technology |
|:---|:---|
| **Frontend Framework** | React 19 + Vite 8 |
| **Language** | TypeScript 6 (strict mode) |
| **UI Library** | Material UI v9 |
| **Data Fetching** | TanStack React Query v5 |
| **Forms & Validation** | React Hook Form + Zod |
| **HTTP Client** | Axios with interceptors |
| **Routing** | React Router DOM v7 |
| **Build Tool** | Vite + esbuild |
| **Linter** | OxLint |
| **Backend** | Spring Boot 3 + Java 21 |
| **Database** | MySQL |
| **Security** | Spring Security + JWT |

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────┐
│                   React Frontend                      │
│   (Vite + TypeScript + React Query + Material UI)    │
│                                                       │
│  ┌──────────┐  ┌──────────┐  ┌─────────────────┐   │
│  │  Patient │  │  Doctor  │  │  Appointment     │   │
│  │  Module  │  │  Module  │  │  Module          │   │
│  └──────────┘  └──────────┘  └─────────────────┘   │
│                    │                                  │
│          Axios + JWT Interceptors                    │
└─────────────────────────────────────────────────────┘
                      │ REST API
┌─────────────────────────────────────────────────────┐
│                Spring Boot Backend                    │
│         (Java 21 + Spring Security + JWT)            │
│                                                       │
│  AuthController │ DoctorController │ PatientController │
│  AppointmentController │ AvailabilityController       │
└─────────────────────────────────────────────────────┘
                      │ JPA/Hibernate
              ┌───────────────────┐
              │   MySQL Database   │
              └───────────────────┘
```

---

## 📸 Screenshots

> *(Add your screenshots below once deployed)*

| Login | Patient Dashboard | Doctor Search |
|:---:|:---:|:---:|
| ![Login](./assets/screenshots/login.png) | ![Dashboard](./assets/screenshots/patient-dashboard.png) | ![Search](./assets/screenshots/doctor-search.png) |

| Doctor Dashboard | Appointment Booking | Appointment Management |
|:---:|:---:|:---:|
| ![Doctor](./assets/screenshots/doctor-dashboard.png) | ![Booking](./assets/screenshots/booking.png) | ![Appointments](./assets/screenshots/appointments.png) |

---

## 📁 Folder Structure

```
src/
├── components/           # Global, reusable UI components
│   ├── ConfirmationDialog.tsx
│   ├── DataTable.tsx
│   ├── ErrorBoundary.tsx
│   ├── LoadingScreen.tsx
│   └── ...
├── context/              # React Contexts (Auth, Theme)
│   ├── AuthContext.tsx
│   └── ThemeContext.tsx
├── features/             # Feature-based modular architecture
│   ├── appointment/      # Booking & appointment lifecycle
│   │   ├── api/          # appointmentApi.ts
│   │   ├── components/   # AppointmentCard, AppointmentTable, SlotPicker
│   │   ├── hooks/        # useAppointments.ts (React Query)
│   │   ├── types/        # appointment.types.ts
│   │   └── utils/        # timeSlotUtils.ts
│   ├── auth/             # Login, Register, JWT handling
│   │   ├── api/
│   │   ├── components/   # ProtectedRoute, RoleGuard
│   │   └── pages/
│   ├── common/           # Cross-feature utilities (Snackbar)
│   ├── doctor/           # Doctor profile & availability
│   │   ├── api/
│   │   ├── hooks/
│   │   ├── pages/        # DoctorDashboard, Profile, Availability, Appointments
│   │   └── types/
│   └── patient/          # Patient profile & doctor search
│       ├── api/
│       ├── hooks/
│       ├── pages/        # PatientDashboard, Profile, Search, Booking
│       └── types/
├── layouts/              # Dashboard layouts (Doctor, Patient, Navbars)
├── pages/                # Global/Error pages (404, 403)
├── routes/               # AppRoutes.tsx (centralized routing)
├── services/             # apiClient.ts (Axios instance + interceptors)
├── theme/                # MUI theme customization
├── types/                # Global type definitions
└── utils/                # tokenStorage.ts, constants.ts, env.ts
```

---

## 🛠 Installation

### Prerequisites
- Node.js v18+
- npm v9+
- A running instance of the backend (see [backend repo](https://github.com/your-username/doctor-appointment-backend))

### Steps

```bash
# 1. Clone the repository
git clone https://github.com/your-username/doctor-appointment-frontend.git
cd doctor-appointment-frontend

# 2. Install dependencies
npm install

# 3. Configure environment variables
cp .env.example .env
# Edit .env and set your backend URL

# 4. Start the development server
npm run dev

# App will be available at http://localhost:5173
```

---

## 🔑 Environment Variables

| Variable | Required | Description |
|:---|:---:|:---|
| `VITE_API_BASE_URL` | ✅ | The base URL of the Spring Boot backend (e.g. `https://your-backend.onrender.com`) |

```env
# .env.example
VITE_API_BASE_URL=http://localhost:8080
```

> ⚠️ **Never commit your `.env` file.** It is excluded via `.gitignore`.

---

## 🚀 Deployment

### Frontend → Vercel *(Recommended)*

1. Push your code to GitHub.
2. Connect the repository to [Vercel](https://vercel.com).
3. Set the environment variable `VITE_API_BASE_URL` in Vercel's project settings.
4. Vercel auto-detects Vite. Ensure the build command is `npm run build` and the output directory is `dist`.
5. The included `vercel.json` handles SPA routing, security headers, and asset caching automatically.

### Backend → Render

The backend is a Spring Boot application deployed on [Render](https://render.com).  
See [Deployment.md](./Deployment.md) for full configuration details.

---

## 📖 API Documentation

Interactive Swagger UI: [`https://doctor-backend-f7nj.onrender.com/swagger-ui/index.html`](https://doctor-backend-f7nj.onrender.com/swagger-ui/index.html)

See [API.md](./API.md) for the full list of integrated and missing endpoints.

---

## 🔮 Future Improvements

| Priority | Feature | Requires |
|:---:|:---|:---|
| 🔴 High | **Admin Dashboard** | New backend APIs (`/admin/*`) |
| 🔴 High | **Slot Collision Prevention** | `GET /availability/doctor/{id}/slots?date=...` |
| 🟡 Medium | **Edit/Delete Availability** | `PUT` & `DELETE /availability/{id}` |
| 🟡 Medium | **HttpOnly Cookie Auth** | Backend security upgrade |
| 🟢 Low | **Real-time Notifications** | WebSocket support |
| 🟢 Low | **Appointment Reminders** | Email integration |

---

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](./LICENSE) file for details.

---

<div align="center">

Made with ❤️ using React, TypeScript, and Spring Boot

</div>
