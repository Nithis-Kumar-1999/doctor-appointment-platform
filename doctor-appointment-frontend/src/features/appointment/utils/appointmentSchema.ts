import { z } from 'zod';

export const appointmentBookingSchema = z.object({
  doctorId: z.number().positive(),
  appointmentDate: z.string().min(1, 'Please select a date'),
  appointmentTime: z.string().min(1, 'Please select a time slot'),
  reason: z.string().min(5, 'Reason must be at least 5 characters').max(500, 'Reason too long')
});

export type AppointmentBookingFormValues = z.infer<typeof appointmentBookingSchema>;
