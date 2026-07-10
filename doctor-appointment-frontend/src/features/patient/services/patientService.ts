import api from '../../../services/api';
import { PatientProfile, PatientDashboardStats } from '../types/patientTypes';
import { Appointment, DoctorProfile } from '../../doctor/types/doctorTypes';

export const patientService = {
  getProfile: async (): Promise<PatientProfile> => {
    const res = await api.get('/api/v1/patients/profile/me');
    return res.data;
  },
  
  updateProfile: async (data: Partial<PatientProfile>): Promise<PatientProfile> => {
    const res = await api.put('/api/v1/patients/profile', data);
    return res.data;
  },

  getDashboardStats: async (): Promise<PatientDashboardStats> => {
    try {
      const res = await api.get('/api/v1/patients/me/stats');
      return res.data;
    } catch {
      // Mock fallback for UI rendering if backend stats endpoint doesn't exist yet
      return {
        upcomingAppointments: 2,
        completedAppointments: 14,
        cancelledAppointments: 1
      };
    }
  },

  getAppointments: async (page = 0, size = 10, status?: string): Promise<{ content: Appointment[], totalElements: number }> => {
    const statusParam = status ? `&status=${status}` : '';
    try {
      const res = await api.get(`/api/v1/appointments/patient/me?page=${page}&size=${size}${statusParam}`);
      return res.data;
    } catch {
      return { content: [], totalElements: 0 };
    }
  },

  findDoctors: async (page = 0, size = 10, specialty?: string, search?: string): Promise<{ content: DoctorProfile[], totalElements: number }> => {
    // Query params for search and filter
    const params = new URLSearchParams();
    params.append('page', page.toString());
    params.append('size', size.toString());
    if (specialty) params.append('specialty', specialty);
    if (search) params.append('search', search);

    try {
      const res = await api.get(`/api/v1/doctors?${params.toString()}`);
      return res.data;
    } catch {
      return { content: [], totalElements: 0 };
    }
  }
};
