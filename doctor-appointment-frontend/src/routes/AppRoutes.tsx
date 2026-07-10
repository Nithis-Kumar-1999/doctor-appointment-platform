import React, { Suspense, lazy } from 'react';
import { Routes, Route } from 'react-router-dom';
import MainLayout from '../layouts/MainLayout';
import DashboardLayout from '../layouts/DashboardLayout';
import AuthGuard from '../features/auth/components/AuthGuard';
import GuestGuard from '../features/auth/components/GuestGuard';
import LoadingOverlay from '../components/LoadingOverlay';

// Lazy loading pages for bundle optimization
const LoginPage = lazy(() => import('../features/auth/pages/LoginPage'));
const RegisterPage = lazy(() => import('../features/auth/pages/RegisterPage'));

const DoctorDashboard = lazy(() => import('../features/doctor/pages/DoctorDashboard'));
const DoctorAppointments = lazy(() => import('../features/doctor/pages/DoctorAppointments'));
const DoctorAvailabilityPage = lazy(() => import('../features/doctor/pages/DoctorAvailability'));
const DoctorProfilePage = lazy(() => import('../features/doctor/pages/DoctorProfile'));

const PatientDashboard = lazy(() => import('../features/patient/pages/PatientDashboard'));
const PatientProfilePage = lazy(() => import('../features/patient/pages/PatientProfile'));
const MyAppointments = lazy(() => import('../features/patient/pages/MyAppointments'));
const FindDoctors = lazy(() => import('../features/patient/pages/FindDoctors'));

const BookAppointmentPage = lazy(() => import('../features/appointment/pages/BookAppointmentPage'));
const AppointmentDetailsPage = lazy(() => import('../features/appointment/pages/AppointmentDetailsPage'));
const AppointmentSuccessPage = lazy(() => import('../features/appointment/pages/AppointmentSuccessPage'));

const NotFound = lazy(() => import('../pages/NotFound'));

/**
 * Global Routing Configuration.
 * Implements lazy loading / code splitting.
 */
const AppRoutes = () => {
  return (
    <Suspense fallback={<LoadingOverlay open={true} message="Loading content..." />}>
      <Routes>
        <Route element={<MainLayout />}>
          
          {/* Public Routes */}
          <Route path="/" element={
            <div style={{ textAlign: 'center', marginTop: '40px' }}>
              <h1>Welcome to CarePortal</h1>
              <p>Please login to book appointments.</p>
            </div>
          } />
          
          {/* Guest Routes (Redirect to dashboard if already logged in) */}
          <Route element={<GuestGuard />}>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
          </Route>
          
          {/* Protected Dashboard Routes */}
          <Route element={<AuthGuard />}>
            <Route element={<DashboardLayout />}>
              {/* Doctor Routes */}
              <Route path="/doctor/dashboard" element={<DoctorDashboard />} />
              <Route path="/doctor/appointments" element={<DoctorAppointments />} />
              <Route path="/doctor/availability" element={<DoctorAvailabilityPage />} />
              <Route path="/doctor/profile" element={<DoctorProfilePage />} />

              {/* Patient Routes */}
              <Route path="/patient/dashboard" element={<PatientDashboard />} />
              <Route path="/patient/appointments" element={<MyAppointments />} />
              <Route path="/patient/find-doctors" element={<FindDoctors />} />
              <Route path="/patient/profile" element={<PatientProfilePage />} />
              
              <Route path="/patient/book-appointment/:doctorId" element={<BookAppointmentPage />} />
              <Route path="/patient/appointment-details/:id" element={<AppointmentDetailsPage />} />
              <Route path="/patient/appointment-success/:id" element={<AppointmentSuccessPage />} />
              
              {/* Fallback to patient dashboard for legacy /dashboard */}
              <Route path="/dashboard" element={<PatientDashboard />} />
            </Route>
          </Route>
          
          {/* 404 Catch-All */}
          <Route path="*" element={<NotFound />} />
        </Route>
      </Routes>
    </Suspense>
  );
};

export default AppRoutes;
