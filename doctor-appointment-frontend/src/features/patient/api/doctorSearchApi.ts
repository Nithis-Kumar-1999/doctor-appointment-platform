import { apiClient } from '../../../services/apiClient';
import { DoctorResponse, Specialty } from '../../doctor/types/doctor.types';
import { PageResponse } from '../../../types/common.types';

export const doctorSearchApi = {
  searchDoctors: async (
    specialty?: Specialty | '',
    city?: string,
    page: number = 0,
    size: number = 10
  ): Promise<PageResponse<DoctorResponse>> => {
    
    const params = new URLSearchParams();
    if (specialty) params.append('specialty', specialty);
    if (city) params.append('city', city);
    params.append('page', page.toString());
    params.append('size', size.toString());

    const response = await apiClient.get<PageResponse<DoctorResponse>>('/api/v1/doctors', { params });
    return response.data;
  }
};
