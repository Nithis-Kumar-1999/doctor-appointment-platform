import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { doctorService } from '../services/doctorService';
import { useSnackbar } from '../../common/SnackbarContext';
import { DoctorProfile } from '../types/doctorTypes';

export const useDoctorProfile = () => {
  return useQuery({
    queryKey: ['doctorProfile'],
    queryFn: () => doctorService.getProfile(),
    retry: 1,
  });
};

export const useUpdateDoctorProfile = () => {
  const queryClient = useQueryClient();
  const { showSnackbar } = useSnackbar();

  return useMutation({
    mutationFn: (data: Partial<DoctorProfile>) => doctorService.updateProfile(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['doctorProfile'] });
      showSnackbar('Profile updated successfully', 'success');
    },
    onError: () => {
      showSnackbar('Failed to update profile', 'error');
    }
  });
};

export const useDoctorDashboardStats = () => {
  return useQuery({
    queryKey: ['doctorStats'],
    queryFn: () => doctorService.getDashboardStats(),
  });
};

export const useDoctorAppointments = (page: number, size: number, status?: string) => {
  return useQuery({
    queryKey: ['doctorAppointments', page, size, status],
    queryFn: () => doctorService.getAppointments(page, size, status),
    placeholderData: (previousData) => previousData, // keepPreviousData
  });
};

export const useUpdateAppointmentStatus = () => {
  const queryClient = useQueryClient();
  const { showSnackbar } = useSnackbar();

  return useMutation({
    mutationFn: ({ id, status }: { id: number, status: string }) => doctorService.updateAppointmentStatus(id, status),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['doctorAppointments'] });
      queryClient.invalidateQueries({ queryKey: ['doctorStats'] });
      showSnackbar('Appointment status updated', 'success');
    },
    onError: () => {
      showSnackbar('Failed to update appointment', 'error');
    }
  });
};

export const useDoctorAvailability = (doctorId?: number) => {
  return useQuery({
    queryKey: ['doctorAvailability', doctorId],
    queryFn: () => doctorService.getAvailability(doctorId!),
    enabled: !!doctorId,
  });
};
