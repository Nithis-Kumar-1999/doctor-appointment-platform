export type Specialty =
  | 'CARDIOLOGY'
  | 'DERMATOLOGY'
  | 'NEUROLOGY'
  | 'ORTHOPEDICS'
  | 'PEDIATRICS'
  | 'PSYCHIATRY'
  | 'GENERAL_MEDICINE'
  | 'GYNECOLOGY'
  | 'ONCOLOGY'
  | 'OPHTHALMOLOGY'
  | 'ENT'
  | 'UROLOGY'
  | 'DENTISTRY'
  | 'PHYSIOTHERAPY';

export interface DoctorRequest {
  specialty: Specialty;
  qualification: string;
  experienceYears: number;
  consultationFee: number;
  phone: string;
  city: string;
  bio?: string;
  profileImageUrl?: string;
}

export interface DoctorResponse {
  id: number;
  userId: number;
  firstName: string;
  lastName: string;
  email: string;
  specialty: Specialty;
  specialtyDisplayName: string;
  qualification: string;
  experienceYears: number;
  consultationFee: number;
  phone: string;
  city: string;
  bio: string | null;
  profileImageUrl: string | null;
  active: boolean;
  createdAt: string;
}

export type DayOfWeek =
  | 'MONDAY'
  | 'TUESDAY'
  | 'WEDNESDAY'
  | 'THURSDAY'
  | 'FRIDAY'
  | 'SATURDAY'
  | 'SUNDAY';

export interface DoctorAvailability {
  id: number;
  dayOfWeek: DayOfWeek;
  startTime: string; // HH:mm:ss format
  endTime: string;   // HH:mm:ss format
  slotDurationMinutes: number;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface AvailabilityRequest {
  dayOfWeek: DayOfWeek;
  startTime: string; // HH:mm:ss
  endTime: string;   // HH:mm:ss
  slotDurationMinutes: number;
}
