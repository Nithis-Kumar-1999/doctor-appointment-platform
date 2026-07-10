export interface PatientProfile {
  id?: number;
  userId?: number;
  dateOfBirth: string;
  gender: 'MALE' | 'FEMALE' | 'OTHER';
  phone: string;
  bloodGroup?: string;
  address?: string;
}

export interface PatientDashboardStats {
  upcomingAppointments: number;
  completedAppointments: number;
  cancelledAppointments: number;
}
