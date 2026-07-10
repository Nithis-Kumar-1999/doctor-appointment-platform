import React, { useState } from 'react';
import PageContainer from '../../../components/PageContainer';
import PageHeader from '../../../components/PageHeader';
import DoctorProfileCard from '../components/DoctorProfileCard';
import InfoCard from '../../../components/InfoCard';
import { useDoctorProfile, useUpdateDoctorProfile } from '../hooks/useDoctorQueries';
import { Box, Dialog, DialogTitle, DialogContent, DialogActions, Button, TextField, Grid } from '@mui/material';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { doctorProfileSchema, DoctorProfileFormValues } from '../utils/doctorSchema';

const DoctorProfilePage = () => {
  const { data: profile, isLoading } = useDoctorProfile();
  const updateMutation = useUpdateDoctorProfile();
  const [editOpen, setEditOpen] = useState(false);

  const { control, handleSubmit, reset } = useForm<DoctorProfileFormValues>({
    resolver: zodResolver(doctorProfileSchema)
  });

  const handleEditClick = () => {
    if (profile) {
      reset({
        specialty: profile.specialty,
        experienceYears: profile.experienceYears,
        consultationFee: profile.consultationFee,
        bio: profile.bio || '',
        qualifications: profile.qualifications || ''
      });
      setEditOpen(true);
    }
  };

  const onSubmit = (data: DoctorProfileFormValues) => {
    updateMutation.mutate(data, {
      onSuccess: () => setEditOpen(false)
    });
  };

  return (
    <PageContainer>
      <PageHeader title="My Profile" breadcrumbs={[{ label: 'Profile' }]} />
      
      <DoctorProfileCard profile={profile} isLoading={isLoading} onEditClick={handleEditClick} />

      {profile && (
        <InfoCard 
          title="Professional Details"
          details={[
            { label: 'Qualifications', value: profile.qualifications },
            { label: 'Specialty', value: profile.specialty },
            { label: 'Experience', value: `${profile.experienceYears} Years` },
            { label: 'Consultation Fee', value: `$${profile.consultationFee}` }
          ]}
        />
      )}

      {/* Edit Dialog */}
      <Dialog open={editOpen} onClose={() => setEditOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Edit Profile</DialogTitle>
        <form onSubmit={handleSubmit(onSubmit)}>
          <DialogContent>
            <Grid container spacing={2} sx={{ mt: 1 }}>
              <Grid item xs={12}>
                <Controller
                  name="specialty"
                  control={control}
                  render={({ field, fieldState }) => (
                    <TextField {...field} fullWidth label="Specialty" error={!!fieldState.error} helperText={fieldState.error?.message} />
                  )}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <Controller
                  name="experienceYears"
                  control={control}
                  render={({ field, fieldState }) => (
                    <TextField {...field} fullWidth type="number" label="Experience (Years)" error={!!fieldState.error} helperText={fieldState.error?.message} />
                  )}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <Controller
                  name="consultationFee"
                  control={control}
                  render={({ field, fieldState }) => (
                    <TextField {...field} fullWidth type="number" label="Consultation Fee ($)" error={!!fieldState.error} helperText={fieldState.error?.message} />
                  )}
                />
              </Grid>
              <Grid item xs={12}>
                <Controller
                  name="qualifications"
                  control={control}
                  render={({ field }) => (
                    <TextField {...field} fullWidth label="Qualifications (e.g., MBBS, MD)" />
                  )}
                />
              </Grid>
              <Grid item xs={12}>
                <Controller
                  name="bio"
                  control={control}
                  render={({ field }) => (
                    <TextField {...field} fullWidth multiline rows={4} label="Biography" />
                  )}
                />
              </Grid>
            </Grid>
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setEditOpen(false)}>Cancel</Button>
            <Button type="submit" variant="contained" disabled={updateMutation.isPending}>Save Changes</Button>
          </DialogActions>
        </form>
      </Dialog>
    </PageContainer>
  );
};

export default DoctorProfilePage;
