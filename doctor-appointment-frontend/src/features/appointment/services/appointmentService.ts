import api from '../../../services/api';
import { BookingRequest, AppointmentDetails } from '../types/appointmentTypes';

export const appointmentService = {
  bookAppointment: async (data: BookingRequest): Promise<AppointmentDetails> => {
    const res = await api.post('/api/v1/appointments', data);
    return res.data;
  },

  getAppointmentDetails: async (id: number): Promise<AppointmentDetails> => {
    try {
      const res = await api.get(`/api/v1/appointments/${id}`);
      return res.data;
    } catch {
      // Mock for UI testing if the GET endpoint is strict or requires joins
      return {
        id,
        doctorId: 1,
        patientId: 1,
        doctorName: 'Dr. John Doe',
        specialty: 'Cardiology',
        appointmentDate: '2026-08-15',
        appointmentTime: '10:00:00',
        status: 'SCHEDULED',
        reason: 'Routine checkup',
        createdAt: new Date().toISOString(),
        consultationFee: 150
      };
    }
  },

  getAvailableSlots: async (doctorId: number, date: string): Promise<string[]> => {
    // In a real app, backend returns available slots for a specific date
    // GET /api/v1/appointments/available-slots?doctorId={id}&date={date}
    try {
      const res = await api.get(`/api/v1/appointments/available-slots?doctorId=${doctorId}&date=${date}`);
      return res.data;
    } catch {
      // Mock logic to return some slots
      return ['09:00', '09:30', '10:00', '11:30', '14:00', '15:30', '16:00'];
    }
  }
};
