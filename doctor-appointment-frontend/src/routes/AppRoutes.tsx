import React, { Suspense } from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { ProtectedRoute } from '../features/auth/components/ProtectedRoute';
import { RoleGuard } from '../features/auth/components/RoleGuard';
import { LoginPage } from '../features/auth/pages/LoginPage';
import { RegisterPage } from '../features/auth/pages/RegisterPage';
import { UnauthorizedPage } from '../pages/errors/UnauthorizedPage';
import { NotFoundPage } from '../pages/errors/NotFoundPage';
import { LoadingScreen } from '../components/LoadingScreen';
import { useAuth } from '../context/AuthContext';

import DashboardLayout from '../layouts/DashboardLayout';
import DoctorDashboardPage from '../features/doctor/pages/DoctorDashboardPage';
import DoctorProfilePage from '../features/doctor/pages/DoctorProfilePage';
import AvailabilityPage from '../features/doctor/pages/AvailabilityPage';

import PatientDashboardLayout from '../layouts/PatientDashboardLayout';
import PatientDashboardPage from '../features/patient/pages/PatientDashboardPage';
import PatientProfilePage from '../features/patient/pages/PatientProfilePage';
import SearchDoctorsPage from '../features/patient/pages/SearchDoctorsPage';
import DoctorDetailsPage from '../features/patient/pages/DoctorDetailsPage';
import PatientAppointmentsPage from '../features/patient/pages/PatientAppointmentsPage';

import DoctorAppointmentsPage from '../features/doctor/pages/DoctorAppointmentsPage';

const AppRoutes: React.FC = () => {
  const { isAuthenticated, user } = useAuth();

  return (
    <Suspense fallback={<LoadingScreen />}>
      <Routes>
        {/* Public Routes */}
        <Route path="/login" element={isAuthenticated ? <Navigate to="/" replace /> : <LoginPage />} />
        <Route path="/register" element={isAuthenticated ? <Navigate to="/" replace /> : <RegisterPage />} />
        
        {/* Root Redirect based on Role */}
        <Route 
          path="/" 
          element={
            !isAuthenticated ? <Navigate to="/login" replace /> :
            user?.role === 'DOCTOR' ? <Navigate to="/doctor/dashboard" replace /> :
            user?.role === 'PATIENT' ? <Navigate to="/patient/dashboard" replace /> :
            <Navigate to="/unauthorized" replace />
          } 
        />

        {/* Protected Doctor Routes */}
        <Route path="/doctor/*" element={
          <ProtectedRoute>
            <RoleGuard allowedRoles={['DOCTOR']}>
              <DashboardLayout />
            </RoleGuard>
          </ProtectedRoute>
        }>
          <Route path="dashboard" element={<DoctorDashboardPage />} />
          <Route path="profile" element={<DoctorProfilePage />} />
          <Route path="availability" element={<AvailabilityPage />} />
          <Route path="appointments" element={<DoctorAppointmentsPage />} />
          {/* Add a catch-all to redirect invalid doctor routes to dashboard */}
          <Route path="*" element={<Navigate to="dashboard" replace />} />
        </Route>

        {/* Protected Patient Routes */}
        <Route path="/patient/*" element={
          <ProtectedRoute>
            <RoleGuard allowedRoles={['PATIENT']}>
              <PatientDashboardLayout />
            </RoleGuard>
          </ProtectedRoute>
        }>
          <Route path="dashboard" element={<PatientDashboardPage />} />
          <Route path="profile" element={<PatientProfilePage />} />
          <Route path="search" element={<SearchDoctorsPage />} />
          <Route path="doctor/:id" element={<DoctorDetailsPage />} />
          <Route path="appointments" element={<PatientAppointmentsPage />} />
          <Route path="*" element={<Navigate to="dashboard" replace />} />
        </Route>

        {/* Error Routes */}
        <Route path="/unauthorized" element={<UnauthorizedPage />} />
        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </Suspense>
  );
};

export default AppRoutes;
