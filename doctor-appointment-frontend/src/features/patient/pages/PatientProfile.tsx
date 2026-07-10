import React, { useState } from 'react';
import PageContainer from '../../../components/PageContainer';
import PageHeader from '../../../components/PageHeader';
import PatientProfileCard from '../components/PatientProfileCard';
import InfoCard from '../../../components/InfoCard';
import { usePatientProfile, useUpdatePatientProfile } from '../hooks/usePatientQueries';
import { Dialog, DialogTitle, DialogContent, DialogActions, Button, TextField, Grid, FormControl, InputLabel, Select, MenuItem, FormHelperText } from '@mui/material';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { patientProfileSchema, PatientProfileFormValues } from '../utils/patientSchema';

const PatientProfilePage = () => {
  const { data: profile, isLoading } = usePatientProfile();
  const updateMutation = useUpdatePatientProfile();
  const [editOpen, setEditOpen] = useState(false);

  const { control, handleSubmit, reset } = useForm<PatientProfileFormValues>({
    resolver: zodResolver(patientProfileSchema)
  });

  const handleEditClick = () => {
    if (profile) {
      reset({
        dateOfBirth: profile.dateOfBirth,
        gender: profile.gender,
        phone: profile.phone,
        bloodGroup: profile.bloodGroup || '',
        address: profile.address || ''
      });
      setEditOpen(true);
    }
  };

  const onSubmit = (data: PatientProfileFormValues) => {
    updateMutation.mutate(data, {
      onSuccess: () => setEditOpen(false)
    });
  };

  return (
    <PageContainer>
      <PageHeader title="My Profile" breadcrumbs={[{ label: 'Profile' }]} />
      
      <PatientProfileCard profile={profile} isLoading={isLoading} onEditClick={handleEditClick} />

      {profile && (
        <InfoCard 
          title="Personal Details"
          details={[
            { label: 'Date of Birth', value: profile.dateOfBirth },
            { label: 'Gender', value: profile.gender },
            { label: 'Phone', value: profile.phone },
            { label: 'Blood Group', value: profile.bloodGroup },
            { label: 'Address', value: profile.address }
          ]}
        />
      )}

      {/* Edit Dialog */}
      <Dialog open={editOpen} onClose={() => setEditOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Edit Profile</DialogTitle>
        <form onSubmit={handleSubmit(onSubmit)}>
          <DialogContent>
            <Grid container spacing={2} sx={{ mt: 1 }}>
              <Grid item xs={12} sm={6}>
                <Controller
                  name="dateOfBirth"
                  control={control}
                  render={({ field, fieldState }) => (
                    <TextField {...field} fullWidth type="date" label="Date of Birth" InputLabelProps={{ shrink: true }} error={!!fieldState.error} helperText={fieldState.error?.message} />
                  )}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <Controller
                  name="gender"
                  control={control}
                  render={({ field, fieldState }) => (
                    <FormControl fullWidth error={!!fieldState.error}>
                      <InputLabel id="gender-label">Gender</InputLabel>
                      <Select {...field} labelId="gender-label" label="Gender">
                        <MenuItem value="MALE">Male</MenuItem>
                        <MenuItem value="FEMALE">Female</MenuItem>
                        <MenuItem value="OTHER">Other</MenuItem>
                      </Select>
                      {fieldState.error && <FormHelperText>{fieldState.error.message}</FormHelperText>}
                    </FormControl>
                  )}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <Controller
                  name="phone"
                  control={control}
                  render={({ field, fieldState }) => (
                    <TextField {...field} fullWidth label="Phone" error={!!fieldState.error} helperText={fieldState.error?.message} />
                  )}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <Controller
                  name="bloodGroup"
                  control={control}
                  render={({ field }) => (
                    <TextField {...field} fullWidth label="Blood Group (e.g., O+)" />
                  )}
                />
              </Grid>
              <Grid item xs={12}>
                <Controller
                  name="address"
                  control={control}
                  render={({ field }) => (
                    <TextField {...field} fullWidth multiline rows={2} label="Address" />
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

export default PatientProfilePage;
