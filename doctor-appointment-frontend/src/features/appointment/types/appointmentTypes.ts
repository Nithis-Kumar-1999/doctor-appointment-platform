export interface BookingRequest {
  doctorId: number;
  patientId: number;
  appointmentDate: string; // YYYY-MM-DD
  appointmentTime: string; // HH:MM:SS
  reason?: string;
}

export interface AppointmentDetails {
  id: number;
  doctorId: number;
  patientId: number;
  doctorName?: string;
  patientName?: string;
  specialty?: string;
  appointmentDate: string;
  appointmentTime: string;
  status: 'SCHEDULED' | 'CONFIRMED' | 'COMPLETED' | 'CANCELLED';
  reason?: string;
  createdAt?: string;
  consultationFee?: number;
}
