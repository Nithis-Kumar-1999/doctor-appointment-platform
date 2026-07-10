import React from 'react';
import PageContainer from '../../../components/PageContainer';
import PageHeader from '../../../components/PageHeader';
import AvailabilityCalendar from '../components/AvailabilityCalendar';
import { useDoctorAvailability, useDoctorProfile } from '../hooks/useDoctorQueries';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { doctorService } from '../services/doctorService';
import { useSnackbar } from '../../common/SnackbarContext';

const DoctorAvailabilityPage = () => {
  const { data: profile } = useDoctorProfile();
  const { data: availabilities, isLoading } = useDoctorAvailability(profile?.id);
  const queryClient = useQueryClient();
  const { showSnackbar } = useSnackbar();

  const addMutation = useMutation({
    mutationFn: doctorService.addAvailability,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['doctorAvailability'] });
      showSnackbar('Availability added', 'success');
    },
    onError: () => {
      showSnackbar('Failed to add availability', 'error');
    }
  });

  return (
    <PageContainer>
      <PageHeader title="My Availability" subtitle="Manage your working hours" breadcrumbs={[{ label: 'Availability' }]} />
      <AvailabilityCalendar 
        availabilities={availabilities || []} 
        isLoading={isLoading} 
        onAdd={(data) => addMutation.mutate({ ...data, doctorId: profile?.id })} 
      />
    </PageContainer>
  );
};

export default DoctorAvailabilityPage;
