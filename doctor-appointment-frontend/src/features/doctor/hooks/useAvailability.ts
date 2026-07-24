import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { availabilityApi } from '../api/availabilityApi';
import { AvailabilityRequest, DoctorAvailability } from '../types/doctor.types';
import { useSnackbar } from '../../../features/common/SnackbarContext';

export const getAvailabilityQueryKey = (doctorId: number) => ['availability', doctorId];

export const useDoctorSchedule = (doctorId: number | undefined) => {
  return useQuery<DoctorAvailability[], Error>({
    queryKey: getAvailabilityQueryKey(doctorId as number),
    queryFn: () => availabilityApi.getDoctorSchedule(doctorId as number),
    enabled: !!doctorId, // Only run if doctorId is defined
    staleTime: 5 * 60 * 1000,
  });
};

export const useAddAvailability = (doctorId: number | undefined) => {
  const queryClient = useQueryClient();
  const { showSnackbar } = useSnackbar();

  return useMutation<void, Error, AvailabilityRequest>({
    mutationFn: availabilityApi.addAvailability,
    onSuccess: () => {
      if (doctorId) {
        queryClient.invalidateQueries({ queryKey: getAvailabilityQueryKey(doctorId) });
      }
      showSnackbar('Availability added successfully!', 'success');
    },
    onError: (error: any) => {
      showSnackbar(error?.response?.data?.message || 'Failed to add availability', 'error');
    },
  });
};
