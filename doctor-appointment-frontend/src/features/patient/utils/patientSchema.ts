import { z } from 'zod';

export const patientProfileSchema = z.object({
  dateOfBirth: z.string().regex(/^\d{4}-\d{2}-\d{2}$/, 'Date of Birth must be YYYY-MM-DD'),
  gender: z.enum(['MALE', 'FEMALE', 'OTHER']),
  phone: z.string().min(10, 'Phone number must be at least 10 digits'),
  bloodGroup: z.string().optional(),
  address: z.string().optional()
});

export type PatientProfileFormValues = z.infer<typeof patientProfileSchema>;
