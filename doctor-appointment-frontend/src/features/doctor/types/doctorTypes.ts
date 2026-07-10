export interface DoctorProfile {
  id?: number;
  userId?: number;
  specialty: string;
  experienceYears: number;
  consultationFee: number;
  bio?: string;
  qualifications?: string;
}

export interface DoctorAvailability {
  id?: number;
  doctorId?: number;
  dayOfWeek: string;
  startTime: string;
  endTime: string;
  isAvailable: boolean;
}

export interface Appointment {
  id: number;
  patientId: number;
  patientName?: string;
  appointmentDate: string;
  appointmentTime: string;
  status: 'SCHEDULED' | 'COMPLETED' | 'CANCELLED' | 'PENDING';
  reason?: string;
}

export interface DoctorDashboardStats {
  totalAppointments: number;
  todayAppointments: number;
  upcomingAppointments: number;
  completedAppointments: number;
  cancelledAppointments: number;
}
