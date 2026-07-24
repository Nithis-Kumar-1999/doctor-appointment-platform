import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { doctorApi } from '../api/doctorApi';
import { DoctorRequest, DoctorResponse } from '../types/doctor.types';
import { useSnackbar } from '../../../features/common/SnackbarContext';

export const DOCTOR_PROFILE_KEY = ['doctorProfile'];

export const useDoctorProfile = () => {
  return useQuery<DoctorResponse, Error>({
    queryKey: DOCTOR_PROFILE_KEY,
    queryFn: doctorApi.getMyProfile,
    retry: (failureCount, error: any) => {
      // Don't retry on 404 (Profile not found - means they need to create one)
      if (error?.response?.status === 404) return false;
      return failureCount < 2;
    },
    staleTime: 5 * 60 * 1000, // 5 minutes
  });
};

export const useCreateDoctorProfile = () => {
  const queryClient = useQueryClient();
  const { showSnackbar } = useSnackbar();

  return useMutation<DoctorResponse, Error, DoctorRequest>({
    mutationFn: doctorApi.createProfile,
    onSuccess: (data) => {
      queryClient.setQueryData(DOCTOR_PROFILE_KEY, data);
      showSnackbar('Profile created successfully!', 'success');
    },
    onError: (error: any) => {
      showSnackbar(error?.response?.data?.message || 'Failed to create profile', 'error');
    },
  });
};

export const useUpdateDoctorProfile = () => {
  const queryClient = useQueryClient();
  const { showSnackbar } = useSnackbar();

  return useMutation<DoctorResponse, Error, DoctorRequest>({
    mutationFn: doctorApi.updateProfile,
    onSuccess: (data) => {
      queryClient.setQueryData(DOCTOR_PROFILE_KEY, data);
      showSnackbar('Profile updated successfully!', 'success');
    },
    onError: (error: any) => {
      showSnackbar(error?.response?.data?.message || 'Failed to update profile', 'error');
    },
  });
};
