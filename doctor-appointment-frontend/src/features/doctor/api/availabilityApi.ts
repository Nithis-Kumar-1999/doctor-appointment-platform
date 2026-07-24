import { apiClient } from '../../../services/apiClient';
import { AvailabilityRequest, DoctorAvailability } from '../types/doctor.types';

export const availabilityApi = {
  getDoctorSchedule: async (doctorId: number): Promise<DoctorAvailability[]> => {
    const response = await apiClient.get<DoctorAvailability[]>(`/api/v1/availability/doctor/${doctorId}`);
    return response.data;
  },

  addAvailability: async (data: AvailabilityRequest): Promise<void> => {
    // The backend uses @RequestParam for addAvailability instead of a JSON request body
    const params = new URLSearchParams({
      dayOfWeek: data.dayOfWeek,
      startTime: data.startTime,
      endTime: data.endTime,
      slotDurationMinutes: data.slotDurationMinutes.toString(),
    });
    
    await apiClient.post(`/api/v1/availability?${params.toString()}`);
  },
};
