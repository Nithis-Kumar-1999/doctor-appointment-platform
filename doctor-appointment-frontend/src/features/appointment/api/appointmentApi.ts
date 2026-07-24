import { apiClient } from '../../../services/apiClient';
import { AppointmentRequest, AppointmentResponse, AppointmentStatus } from '../types/appointment.types';
import { PageResponse } from '../../../types/common.types';

export const appointmentApi = {
  bookAppointment: async (data: AppointmentRequest): Promise<AppointmentResponse> => {
    const response = await apiClient.post<AppointmentResponse>('/api/v1/appointments', data);
    return response.data;
  },

  getPatientAppointments: async (
    status?: AppointmentStatus | '',
    page: number = 0,
    size: number = 20
  ): Promise<PageResponse<AppointmentResponse>> => {
    const params = new URLSearchParams();
    if (status) params.append('status', status);
    params.append('page', page.toString());
    params.append('size', size.toString());

    const response = await apiClient.get<PageResponse<AppointmentResponse>>('/api/v1/appointments/patient/me', { params });
    return response.data;
  },

  getDoctorAppointments: async (
    status?: AppointmentStatus | '',
    page: number = 0,
    size: number = 20
  ): Promise<PageResponse<AppointmentResponse>> => {
    const params = new URLSearchParams();
    if (status) params.append('status', status);
    params.append('page', page.toString());
    params.append('size', size.toString());

    const response = await apiClient.get<PageResponse<AppointmentResponse>>('/api/v1/appointments/doctor/me', { params });
    return response.data;
  },

  updateStatus: async (
    appointmentId: number,
    status: AppointmentStatus,
    reasonOrNotes?: string
  ): Promise<AppointmentResponse> => {
    const params = new URLSearchParams();
    params.append('status', status);
    if (reasonOrNotes) params.append('reasonOrNotes', reasonOrNotes);

    const response = await apiClient.patch<AppointmentResponse>(
      `/api/v1/appointments/${appointmentId}/status`, 
      null, 
      { params }
    );
    return response.data;
  }
};
