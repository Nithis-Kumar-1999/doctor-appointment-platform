import { useQuery } from '@tanstack/react-query';
import { doctorSearchApi } from '../api/doctorSearchApi';
import { Specialty } from '../../doctor/types/doctor.types';

export const useDoctorSearch = (specialty?: Specialty | '', city?: string, page: number = 0, size: number = 10) => {
  return useQuery({
    queryKey: ['doctors', specialty, city, page, size],
    queryFn: () => doctorSearchApi.searchDoctors(specialty, city, page, size),
    placeholderData: (previousData) => previousData, // keep previous data while fetching next page
  });
};
