import { z } from 'zod';

export const doctorProfileSchema = z.object({
  specialty: z.string().min(2, 'Specialty is required'),
  experienceYears: z.coerce.number().min(0, 'Experience must be at least 0'),
  consultationFee: z.coerce.number().min(0, 'Consultation fee cannot be negative'),
  bio: z.string().optional(),
  qualifications: z.string().optional(),
});

export type DoctorProfileFormValues = z.infer<typeof doctorProfileSchema>;

export const availabilitySchema = z.object({
  dayOfWeek: z.enum(['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY']),
  startTime: z.string().regex(/^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$/, 'Invalid time format (HH:MM)'),
  endTime: z.string().regex(/^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$/, 'Invalid time format (HH:MM)'),
});

export type AvailabilityFormValues = z.infer<typeof availabilitySchema>;
