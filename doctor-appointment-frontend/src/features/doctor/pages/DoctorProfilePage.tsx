import React, { useEffect, useState } from 'react';
import { Box, Typography, Paper, TextField, Button, Grid, MenuItem, CircularProgress, Alert } from '@mui/material';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useDoctorProfile, useCreateDoctorProfile, useUpdateDoctorProfile } from '../hooks/useDoctorProfile';
import { Specialty, DoctorRequest } from '../types/doctor.types';

// Valid specialties matching backend Enum
const SPECIALTIES: Specialty[] = [
  'CARDIOLOGY', 'DERMATOLOGY', 'NEUROLOGY', 'ORTHOPEDICS', 'PEDIATRICS',
  'PSYCHIATRY', 'GENERAL_MEDICINE', 'GYNECOLOGY', 'ONCOLOGY', 'OPHTHALMOLOGY',
  'ENT', 'UROLOGY', 'DENTISTRY', 'PHYSIOTHERAPY'
];

const profileSchema = z.object({
  specialty: z.enum([
    'CARDIOLOGY', 'DERMATOLOGY', 'NEUROLOGY', 'ORTHOPEDICS', 'PEDIATRICS',
    'PSYCHIATRY', 'GENERAL_MEDICINE', 'GYNECOLOGY', 'ONCOLOGY', 'OPHTHALMOLOGY',
    'ENT', 'UROLOGY', 'DENTISTRY', 'PHYSIOTHERAPY'
  ], { message: 'Specialty is required' }),
  qualification: z.string().min(1, 'Qualification is required').max(200, 'Max 200 characters'),
  experienceYears: z.number().min(0, 'Cannot be negative').max(60, 'Max 60 years'),
  consultationFee: z.number().min(0.01, 'Must be greater than 0'),
  phone: z.string().min(1, 'Phone is required').regex(/^[+]?[(]?[0-9]{1,4}[)]?[-\s./0-9]{8,14}$/, 'Invalid phone format'),
  city: z.string().min(1, 'City is required').max(100, 'Max 100 characters'),
  bio: z.string().max(2000, 'Max 2000 characters').optional().nullable(),
  profileImageUrl: z.string().max(512).optional().nullable(),
});

type ProfileFormValues = z.infer<typeof profileSchema>;

const DoctorProfilePage: React.FC = () => {
  const { data: profile, isLoading, isError, error } = useDoctorProfile();
  const createMutation = useCreateDoctorProfile();
  const updateMutation = useUpdateDoctorProfile();
  const [isEditing, setIsEditing] = useState(false);

  const {
    register,
    handleSubmit,
    control,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<ProfileFormValues>({
    resolver: zodResolver(profileSchema),
    defaultValues: {
      specialty: 'GENERAL_MEDICINE',
      qualification: '',
      experienceYears: 0,
      consultationFee: 0,
      phone: '',
      city: '',
      bio: '',
      profileImageUrl: '',
    },
  });

  // Populate form when profile data loads
  useEffect(() => {
    if (profile) {
      reset({
        specialty: profile.specialty,
        qualification: profile.qualification,
        experienceYears: profile.experienceYears,
        consultationFee: profile.consultationFee,
        phone: profile.phone,
        city: profile.city,
        bio: profile.bio,
        profileImageUrl: profile.profileImageUrl,
      });
    } else if (isError && (error as any)?.response?.status === 404) {
      // 404 means no profile exists yet; default to editing mode
      setIsEditing(true);
    }
  }, [profile, isError, reset, error]);

  if (isLoading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', p: 4 }}>
        <CircularProgress />
      </Box>
    );
  }

  const hasProfile = !!profile;
  const isSubmitLoading = createMutation.isPending || updateMutation.isPending;

  const onSubmit = (data: ProfileFormValues) => {
    const payload: DoctorRequest = {
      ...data,
      bio: data.bio || undefined,
      profileImageUrl: data.profileImageUrl || undefined,
    };

    if (hasProfile) {
      updateMutation.mutate(payload, {
        onSuccess: () => setIsEditing(false)
      });
    } else {
      createMutation.mutate(payload, {
        onSuccess: () => setIsEditing(false)
      });
    }
  };

  return (
    <Box sx={{ p: 3, maxWidth: 800, mx: 'auto' }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" sx={{ fontWeight: 'bold' }}>
          My Profile
        </Typography>
        {hasProfile && !isEditing && (
          <Button variant="contained" color="primary" onClick={() => setIsEditing(true)}>
            Edit Profile
          </Button>
        )}
      </Box>

      {isError && (error as any)?.response?.status !== 404 && (
        <Alert severity="error" sx={{ mb: 3 }}>
          Failed to load profile. Please try again later.
        </Alert>
      )}

      {!hasProfile && !isEditing && !isError && (
         <Alert severity="info" sx={{ mb: 3 }}>
            You haven't set up your profile yet. Please fill in the details below.
         </Alert>
      )}

      <Paper elevation={3} sx={{ p: 4 }}>
        <form onSubmit={handleSubmit(onSubmit)}>
          <Grid container spacing={3}>
            {/* Specialty */}
            <Grid size={{ xs: 12, sm: 6 }}>
              <Controller
                name="specialty"
                control={control}
                render={({ field }) => (
                  <TextField
                    {...field}
                    select
                    fullWidth
                    label="Specialty"
                    error={!!errors.specialty}
                    helperText={errors.specialty?.message}
                    disabled={!isEditing}
                  >
                    {SPECIALTIES.map((spec) => (
                      <MenuItem key={spec} value={spec}>
                        {spec.replace('_', ' ')}
                      </MenuItem>
                    ))}
                  </TextField>
                )}
              />
            </Grid>

            {/* Qualification */}
            <Grid size={{ xs: 12, sm: 6 }}>
              <TextField
                fullWidth
                label="Qualification"
                {...register('qualification')}
                error={!!errors.qualification}
                helperText={errors.qualification?.message}
                disabled={!isEditing}
                placeholder="e.g., MBBS, MD"
              />
            </Grid>

            {/* Experience Years */}
            <Grid size={{ xs: 12, sm: 6 }}>
              <TextField
                fullWidth
                type="number"
                label="Years of Experience"
                {...register('experienceYears', { valueAsNumber: true })}
                error={!!errors.experienceYears}
                helperText={errors.experienceYears?.message}
                disabled={!isEditing}
              />
            </Grid>

            {/* Consultation Fee */}
            <Grid size={{ xs: 12, sm: 6 }}>
              <TextField
                fullWidth
                type="number"
                label="Consultation Fee (INR)"
                {...register('consultationFee', { valueAsNumber: true })}
                error={!!errors.consultationFee}
                helperText={errors.consultationFee?.message}
                disabled={!isEditing}
              />
            </Grid>

            {/* Phone */}
            <Grid size={{ xs: 12, sm: 6 }}>
              <TextField
                fullWidth
                label="Phone Number"
                {...register('phone')}
                error={!!errors.phone}
                helperText={errors.phone?.message}
                disabled={!isEditing}
              />
            </Grid>

            {/* City */}
            <Grid size={{ xs: 12, sm: 6 }}>
              <TextField
                fullWidth
                label="City"
                {...register('city')}
                error={!!errors.city}
                helperText={errors.city?.message}
                disabled={!isEditing}
              />
            </Grid>

            {/* Bio */}
            <Grid size={{ xs: 12 }}>
              <TextField
                fullWidth
                multiline
                rows={4}
                label="Professional Biography"
                {...register('bio')}
                error={!!errors.bio}
                helperText={errors.bio?.message}
                disabled={!isEditing}
              />
            </Grid>

            {/* Action Buttons */}
            {isEditing && (
              <Grid size={{ xs: 12 }} sx={{ display: 'flex', gap: 2, justifyContent: 'flex-end', mt: 2 }}>
                {hasProfile && (
                  <Button 
                    variant="outlined" 
                    color="inherit" 
                    onClick={() => {
                      reset();
                      setIsEditing(false);
                    }}
                    disabled={isSubmitLoading}
                  >
                    Cancel
                  </Button>
                )}
                <Button 
                  type="submit" 
                  variant="contained" 
                  color="primary"
                  disabled={isSubmitting || isSubmitLoading}
                >
                  {isSubmitLoading ? 'Saving...' : hasProfile ? 'Update Profile' : 'Create Profile'}
                </Button>
              </Grid>
            )}
          </Grid>
        </form>
      </Paper>
    </Box>
  );
};

export default DoctorProfilePage;
