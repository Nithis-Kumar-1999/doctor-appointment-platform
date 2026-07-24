import { DoctorAvailability } from '../../doctor/types/doctor.types';

/**
 * Generates a list of time strings (HH:mm:ss) between a start and end time,
 * given a specific duration in minutes.
 * 
 * Note: Since we lack a backend API to fetch currently booked slots for a specific date,
 * this function assumes all slots in the doctor's weekly schedule are available.
 * The backend handles the validation at booking time (returning 409 Conflict if already booked).
 */
export const generateTimeSlots = (
  availability: DoctorAvailability | undefined
): string[] => {
  if (!availability || !availability.active) return [];

  const slots: string[] = [];
  const start = parseTimeToMinutes(availability.startTime);
  const end = parseTimeToMinutes(availability.endTime);
  const duration = availability.slotDurationMinutes;

  if (duration <= 0) return [];

  for (let current = start; current + duration <= end; current += duration) {
    slots.push(formatMinutesToTime(current));
  }

  return slots;
};

const parseTimeToMinutes = (timeString: string): number => {
  const [hours, minutes] = timeString.split(':').map(Number);
  return (hours * 60) + minutes;
};

const formatMinutesToTime = (totalMinutes: number): string => {
  const hours = Math.floor(totalMinutes / 60);
  const minutes = totalMinutes % 60;
  
  const formattedHours = hours.toString().padStart(2, '0');
  const formattedMinutes = minutes.toString().padStart(2, '0');
  
  // Backend expects HH:mm:ss format
  return `${formattedHours}:${formattedMinutes}:00`;
};

/**
 * Helper to determine day of week for a given date string (YYYY-MM-DD)
 */
export const getDayOfWeek = (dateString: string): string => {
  const date = new Date(dateString);
  const days = ['SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY'];
  return days[date.getUTCDay()]; // using UTC to avoid timezone shifting the day
};
