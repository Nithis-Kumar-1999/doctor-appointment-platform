import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { patientApi } from '../api/patientApi';
import { PatientRequest } from '../types/patient.types';
import { useSnackbar } from '../../common/SnackbarContext';

export const usePatientProfile = () => {
  return useQuery({
    queryKey: ['patientProfile'],
    queryFn: patientApi.getMyProfile,
    retry: false, // Don't retry if 404
  });
};

export const useCreatePatientProfile = () => {
  const queryClient = useQueryClient();
  const { showSnackbar } = useSnackbar();

  return useMutation({
    mutationFn: (data: PatientRequest) => patientApi.createProfile(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['patientProfile'] });
      showSnackbar('Profile created successfully', 'success');
    },
    onError: (error: any) => {
      showSnackbar(error.response?.data?.message || 'Failed to create profile', 'error');
    },
  });
};

export const useUpdatePatientProfile = () => {
  const queryClient = useQueryClient();
  const { showSnackbar } = useSnackbar();

  return useMutation({
    mutationFn: (data: PatientRequest) => patientApi.updateProfile(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['patientProfile'] });
      showSnackbar('Profile updated successfully', 'success');
    },
    onError: (error: any) => {
      showSnackbar(error.response?.data?.message || 'Failed to update profile', 'error');
    },
  });
};
