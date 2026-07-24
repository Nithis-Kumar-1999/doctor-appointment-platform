import React, { useEffect } from 'react';
import { Box, Typography, Paper, TextField, Button, CircularProgress, Alert, MenuItem } from '@mui/material';
import { Grid } from '@mui/material';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { usePatientProfile, useCreatePatientProfile, useUpdatePatientProfile } from '../hooks/usePatientProfile';
import { PatientRequest } from '../types/patient.types';

const patientProfileSchema = z.object({
  dateOfBirth: z.string().min(1, 'Date of birth is required'),
  gender: z.enum(['MALE', 'FEMALE', 'OTHER'], { message: 'Gender is required' }),
  phone: z.string().regex(/^[+]?[(]?[0-9]{1,4}[)]?[-\s./0-9]{8,14}$/, 'Invalid phone number format'),
  address: z.string().max(500, 'Max 500 characters').optional(),
  bloodGroup: z.string().max(5, 'Max 5 characters').optional(),
  emergencyContact: z.string()
    .regex(/^[+]?[(]?[0-9]{1,4}[)]?[-\s./0-9]{8,14}$/, 'Invalid phone number format')
    .optional()
    .or(z.literal('')),
});

export const PatientProfilePage: React.FC = () => {
  const { data: profile, isLoading, isError } = usePatientProfile();
  const createMutation = useCreatePatientProfile();
  const updateMutation = useUpdatePatientProfile();

  const isEditing = !!profile;
  const isSaving = createMutation.isPending || updateMutation.isPending;

  const {
    register,
    handleSubmit,
    control,
    reset,
    formState: { errors },
  } = useForm<PatientRequest>({
    resolver: zodResolver(patientProfileSchema),
    defaultValues: {
      dateOfBirth: '',
      gender: 'MALE',
      phone: '',
      address: '',
      bloodGroup: '',
      emergencyContact: '',
    },
  });

  useEffect(() => {
    if (profile) {
      reset({
        dateOfBirth: profile.dateOfBirth,
        gender: profile.gender as any,
        phone: profile.phone,
        address: profile.address || '',
        bloodGroup: profile.bloodGroup || '',
        emergencyContact: profile.emergencyContact || '',
      });
    }
  }, [profile, reset]);

  const onSubmit = (data: PatientRequest) => {
    const payload = { ...data };
    if (!payload.address) delete payload.address;
    if (!payload.bloodGroup) delete payload.bloodGroup;
    if (!payload.emergencyContact) delete payload.emergencyContact;

    if (isEditing) {
      updateMutation.mutate(payload);
    } else {
      createMutation.mutate(payload);
    }
  };

  if (isLoading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', p: 4 }}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3, maxWidth: 800, mx: 'auto' }}>
      <Typography variant="h4" sx={{ fontWeight: 'bold', mb: 3 }}>
        {isEditing ? 'My Profile' : 'Create Profile'}
      </Typography>

      {isError && !isEditing && (
        <Alert severity="info" sx={{ mb: 3 }}>
          Please complete your profile details to unlock all features.
        </Alert>
      )}

      <Paper elevation={3} sx={{ p: 4 }}>
        <form onSubmit={handleSubmit(onSubmit)} noValidate>
          <Grid container spacing={3}>
            <Grid size={{ xs: 12, sm: 6 }}>
              <TextField
                fullWidth
                type="date"
                label="Date of Birth"
                required
                slotProps={{ inputLabel: { shrink: true } }}
                {...register('dateOfBirth')}
                error={!!errors.dateOfBirth}
                helperText={errors.dateOfBirth?.message}
              />
            </Grid>

            <Grid size={{ xs: 12, sm: 6 }}>
              <Controller
                name="gender"
                control={control}
                render={({ field }) => (
                  <TextField
                    {...field}
                    select
                    fullWidth
                    label="Gender"
                    required
                    error={!!errors.gender}
                    helperText={errors.gender?.message}
                  >
                    <MenuItem value="MALE">Male</MenuItem>
                    <MenuItem value="FEMALE">Female</MenuItem>
                    <MenuItem value="OTHER">Other</MenuItem>
                  </TextField>
                )}
              />
            </Grid>

            <Grid size={{ xs: 12, sm: 6 }}>
              <TextField
                fullWidth
                label="Phone Number"
                required
                {...register('phone')}
                error={!!errors.phone}
                helperText={errors.phone?.message}
              />
            </Grid>

            <Grid size={{ xs: 12, sm: 6 }}>
              <TextField
                fullWidth
                label="Blood Group"
                placeholder="e.g. A+"
                {...register('bloodGroup')}
                error={!!errors.bloodGroup}
                helperText={errors.bloodGroup?.message}
              />
            </Grid>

            <Grid size={{ xs: 12 }}>
              <TextField
                fullWidth
                label="Address"
                multiline
                rows={3}
                {...register('address')}
                error={!!errors.address}
                helperText={errors.address?.message}
              />
            </Grid>

            <Grid size={{ xs: 12, sm: 6 }}>
              <TextField
                fullWidth
                label="Emergency Contact (Phone)"
                {...register('emergencyContact')}
                error={!!errors.emergencyContact}
                helperText={errors.emergencyContact?.message}
              />
            </Grid>

            <Grid size={{ xs: 12 }} sx={{ display: 'flex', justifyContent: 'flex-end', mt: 2 }}>
              <Button
                type="submit"
                variant="contained"
                color="primary"
                size="large"
                disabled={isSaving}
              >
                {isSaving ? <CircularProgress size={24} /> : (isEditing ? 'Save Changes' : 'Create Profile')}
              </Button>
            </Grid>
          </Grid>
        </form>
      </Paper>
    </Box>
  );
};

export default PatientProfilePage;
