import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { appointmentService } from '../services/appointmentService';
import { useSnackbar } from '../../common/SnackbarContext';
import { BookingRequest } from '../types/appointmentTypes';
import { useNavigate } from 'react-router-dom';

export const useAvailableSlots = (doctorId: number, date: string) => {
  return useQuery({
    queryKey: ['availableSlots', doctorId, date],
    queryFn: () => appointmentService.getAvailableSlots(doctorId, date),
    enabled: !!doctorId && !!date,
  });
};

export const useBookAppointment = () => {
  const queryClient = useQueryClient();
  const { showSnackbar } = useSnackbar();
  const navigate = useNavigate();

  return useMutation({
    mutationFn: (data: BookingRequest) => appointmentService.bookAppointment(data),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ['patientAppointments'] });
      showSnackbar('Appointment booked successfully!', 'success');
      navigate(`/patient/appointment-success/${data.id}`);
    },
    onError: () => {
      showSnackbar('Failed to book appointment', 'error');
    }
  });
};

export const useAppointmentDetails = (id: number) => {
  return useQuery({
    queryKey: ['appointmentDetails', id],
    queryFn: () => appointmentService.getAppointmentDetails(id),
    enabled: !!id,
  });
};
