import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { appointmentApi } from '../api/appointmentApi';
import { AppointmentRequest, AppointmentStatus } from '../types/appointment.types';
import { useSnackbar } from '../../common/SnackbarContext';

export const useBookAppointment = () => {
  const queryClient = useQueryClient();
  const { showSnackbar } = useSnackbar();

  return useMutation({
    mutationFn: (data: AppointmentRequest) => appointmentApi.bookAppointment(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['patientAppointments'] });
      showSnackbar('Appointment booked successfully!', 'success');
    },
    onError: (error: any) => {
      showSnackbar(error.response?.data?.message || 'Failed to book appointment. It may be already taken.', 'error');
    }
  });
};

export const usePatientAppointments = (status?: AppointmentStatus | '', page: number = 0, size: number = 20) => {
  return useQuery({
    queryKey: ['patientAppointments', status, page, size],
    queryFn: () => appointmentApi.getPatientAppointments(status, page, size),
    placeholderData: (prev) => prev,
  });
};

export const useDoctorAppointments = (status?: AppointmentStatus | '', page: number = 0, size: number = 20) => {
  return useQuery({
    queryKey: ['doctorAppointments', status, page, size],
    queryFn: () => appointmentApi.getDoctorAppointments(status, page, size),
    placeholderData: (prev) => prev,
  });
};

export const useUpdateAppointmentStatus = () => {
  const queryClient = useQueryClient();
  const { showSnackbar } = useSnackbar();

  return useMutation({
    mutationFn: ({ id, status, reason }: { id: number, status: AppointmentStatus, reason?: string }) => 
      appointmentApi.updateStatus(id, status, reason),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['patientAppointments'] });
      queryClient.invalidateQueries({ queryKey: ['doctorAppointments'] });
      showSnackbar('Status updated successfully', 'success');
    },
    onError: (error: any) => {
      showSnackbar(error.response?.data?.message || 'Failed to update status', 'error');
    }
  });
};
