import { apiClient } from '../../../services/apiClient';
import { DoctorRequest, DoctorResponse } from '../types/doctor.types';

export const doctorApi = {
  createProfile: async (data: DoctorRequest): Promise<DoctorResponse> => {
    const response = await apiClient.post<DoctorResponse>('/api/v1/doctors/profile', data);
    return response.data;
  },

  updateProfile: async (data: DoctorRequest): Promise<DoctorResponse> => {
    const response = await apiClient.put<DoctorResponse>('/api/v1/doctors/profile', data);
    return response.data;
  },

  getMyProfile: async (): Promise<DoctorResponse> => {
    const response = await apiClient.get<DoctorResponse>('/api/v1/doctors/profile/me');
    return response.data;
  },
};
