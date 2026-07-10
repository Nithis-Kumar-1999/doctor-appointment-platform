import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { patientService } from '../services/patientService';
import { useSnackbar } from '../../common/SnackbarContext';
import { PatientProfile } from '../types/patientTypes';

export const usePatientProfile = () => {
  return useQuery({
    queryKey: ['patientProfile'],
    queryFn: () => patientService.getProfile(),
    retry: 1,
  });
};

export const useUpdatePatientProfile = () => {
  const queryClient = useQueryClient();
  const { showSnackbar } = useSnackbar();

  return useMutation({
    mutationFn: (data: Partial<PatientProfile>) => patientService.updateProfile(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['patientProfile'] });
      showSnackbar('Profile updated successfully', 'success');
    },
    onError: () => {
      showSnackbar('Failed to update profile', 'error');
    }
  });
};

export const usePatientDashboardStats = () => {
  return useQuery({
    queryKey: ['patientStats'],
    queryFn: () => patientService.getDashboardStats(),
  });
};

export const usePatientAppointments = (page: number, size: number, status?: string) => {
  return useQuery({
    queryKey: ['patientAppointments', page, size, status],
    queryFn: () => patientService.getAppointments(page, size, status),
    placeholderData: (prev) => prev,
  });
};

export const useFindDoctors = (page: number, size: number, specialty?: string, search?: string) => {
  return useQuery({
    queryKey: ['findDoctors', page, size, specialty, search],
    queryFn: () => patientService.findDoctors(page, size, specialty, search),
    placeholderData: (prev) => prev,
  });
};
