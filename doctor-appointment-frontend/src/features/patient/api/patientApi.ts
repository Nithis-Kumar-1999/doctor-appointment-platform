import { apiClient } from '../../../services/apiClient';
import { PatientRequest, PatientResponse } from '../types/patient.types';

export const patientApi = {
  createProfile: async (data: PatientRequest): Promise<PatientResponse> => {
    const response = await apiClient.post<PatientResponse>('/api/v1/patients/profile', data);
    return response.data;
  },

  updateProfile: async (data: PatientRequest): Promise<PatientResponse> => {
    const response = await apiClient.put<PatientResponse>('/api/v1/patients/profile', data);
    return response.data;
  },

  getMyProfile: async (): Promise<PatientResponse> => {
    const response = await apiClient.get<PatientResponse>('/api/v1/patients/profile/me');
    return response.data;
  },
};
