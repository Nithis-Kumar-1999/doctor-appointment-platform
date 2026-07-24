export type Gender = 'MALE' | 'FEMALE' | 'OTHER';

export interface PatientRequest {
  dateOfBirth: string; // YYYY-MM-DD format required by backend
  gender: Gender;
  phone: string;
  address?: string;
  bloodGroup?: string;
  emergencyContact?: string;
}

export interface PatientResponse {
  id: number;
  userId: number;
  firstName: string;
  lastName: string;
  email: string;
  dateOfBirth: string;
  gender: string;
  genderDisplayName: string;
  phone: string;
  address: string | null;
  bloodGroup: string | null;
  emergencyContact: string | null;
  active: boolean;
  createdAt: string;
}

// Re-using DoctorResponse from doctor feature, or defining it here.
// Better to define here to avoid tight coupling or import it.
