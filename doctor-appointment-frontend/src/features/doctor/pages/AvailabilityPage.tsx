import React, { useState } from 'react';
import { 
  Box, Typography, Paper, Grid, TextField, MenuItem, Button, 
  Table, TableBody, TableCell, TableContainer, TableHead, TableRow, 
  CircularProgress, Alert, Chip 
} from '@mui/material';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useDoctorSchedule, useAddAvailability } from '../hooks/useAvailability';
import { useDoctorProfile } from '../hooks/useDoctorProfile';
import { DayOfWeek, AvailabilityRequest } from '../types/doctor.types';

const DAYS_OF_WEEK: DayOfWeek[] = [
  'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'
];

const availabilitySchema = z.object({
  dayOfWeek: z.enum(['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'], { message: 'Day is required' }),
  startTime: z.string().regex(/^([0-1][0-9]|2[0-3]):[0-5][0-9](:[0-5][0-9])?$/, 'Invalid time format (HH:mm)'),
  endTime: z.string().regex(/^([0-1][0-9]|2[0-3]):[0-5][0-9](:[0-5][0-9])?$/, 'Invalid time format (HH:mm)'),
  slotDurationMinutes: z.number().min(10, 'Minimum 10 mins').max(480, 'Maximum 480 mins'),
}).refine(data => {
  // Add a simple check to ensure start < end
  const start = new Date(`1970-01-01T${data.startTime}:00`);
  const end = new Date(`1970-01-01T${data.endTime}:00`);
  return start < end;
}, {
  message: "End time must be after start time",
  path: ['endTime'],
});

type AvailabilityFormValues = z.infer<typeof availabilitySchema>;

const AvailabilityPage: React.FC = () => {
  // We need the doctorId to fetch the schedule. 
  // Getting it from useDoctorProfile()
  const { data: profile, isLoading: isProfileLoading } = useDoctorProfile();
  const doctorId = profile?.id;

  const { data: schedule, isLoading: isScheduleLoading, isError } = useDoctorSchedule(doctorId);
  const addMutation = useAddAvailability(doctorId);
  
  const [isFormOpen, setIsFormOpen] = useState(false);

  const {
    register,
    handleSubmit,
    control,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<AvailabilityFormValues>({
    resolver: zodResolver(availabilitySchema),
    defaultValues: {
      dayOfWeek: 'MONDAY',
      startTime: '09:00',
      endTime: '17:00',
      slotDurationMinutes: 30,
    },
  });

  const onSubmit = (data: AvailabilityFormValues) => {
    // Ensure seconds are appended if not present
    const payload: AvailabilityRequest = {
      ...data,
      startTime: data.startTime.length === 5 ? `${data.startTime}:00` : data.startTime,
      endTime: data.endTime.length === 5 ? `${data.endTime}:00` : data.endTime,
    };

    addMutation.mutate(payload, {
      onSuccess: () => {
        reset();
        setIsFormOpen(false);
      }
    });
  };

  if (isProfileLoading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', p: 4 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (!profile) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="warning">
          You must create your Doctor Profile first before managing your availability.
        </Alert>
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3, maxWidth: 1000, mx: 'auto' }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" sx={{ fontWeight: 'bold' }}>
          Weekly Schedule
        </Typography>
        <Button 
          variant="contained" 
          color="primary" 
          onClick={() => setIsFormOpen(!isFormOpen)}
        >
          {isFormOpen ? 'Cancel' : '+ Add Availability'}
        </Button>
      </Box>

      {isFormOpen && (
        <Paper elevation={3} sx={{ p: 3, mb: 4 }}>
          <Typography variant="h6" gutterBottom>Add New Availability Slot</Typography>
          <form onSubmit={handleSubmit(onSubmit)}>
            <Grid container spacing={3}>
              <Grid size={{ xs: 12, sm: 6, md: 3 }}>
                <Controller
                  name="dayOfWeek"
                  control={control}
                  render={({ field }) => (
                    <TextField
                      {...field}
                      select
                      fullWidth
                      label="Day of Week"
                      error={!!errors.dayOfWeek}
                      helperText={errors.dayOfWeek?.message}
                    >
                      {DAYS_OF_WEEK.map((day) => (
                        <MenuItem key={day} value={day}>
                          {day}
                        </MenuItem>
                      ))}
                    </TextField>
                  )}
                />
              </Grid>

              <Grid size={{ xs: 12, sm: 6, md: 3 }}>
                <TextField
                  fullWidth
                  type="time"
                  label="Start Time"
                  slotProps={{ inputLabel: { shrink: true } }}
                  {...register('startTime')}
                  error={!!errors.startTime}
                  helperText={errors.startTime?.message}
                />
              </Grid>

              <Grid size={{ xs: 12, sm: 6, md: 3 }}>
                <TextField
                  fullWidth
                  type="time"
                  label="End Time"
                  slotProps={{ inputLabel: { shrink: true } }}
                  {...register('endTime')}
                  error={!!errors.endTime}
                  helperText={errors.endTime?.message}
                />
              </Grid>

              <Grid size={{ xs: 12, sm: 6, md: 3 }}>
                <TextField
                  fullWidth
                  type="number"
                  label="Slot Duration (mins)"
                  {...register('slotDurationMinutes', { valueAsNumber: true })}
                  error={!!errors.slotDurationMinutes}
                  helperText={errors.slotDurationMinutes?.message}
                />
              </Grid>

              <Grid size={{ xs: 12 }} sx={{ display: 'flex', justifyContent: 'flex-end' }}>
                <Button 
                  type="submit" 
                  variant="contained" 
                  color="primary"
                  disabled={isSubmitting || addMutation.isPending}
                >
                  {addMutation.isPending ? 'Adding...' : 'Save Availability'}
                </Button>
              </Grid>
            </Grid>
          </form>
        </Paper>
      )}

      {isError && (
        <Alert severity="error" sx={{ mb: 3 }}>
          Failed to load schedule.
        </Alert>
      )}

      <TableContainer component={Paper} elevation={2}>
        <Table sx={{ minWidth: 650 }} aria-label="schedule table">
          <TableHead sx={{ bgcolor: 'grey.100' }}>
            <TableRow>
              <TableCell sx={{ fontWeight: 'bold' }}>Day</TableCell>
              <TableCell sx={{ fontWeight: 'bold' }}>Start Time</TableCell>
              <TableCell sx={{ fontWeight: 'bold' }}>End Time</TableCell>
              <TableCell sx={{ fontWeight: 'bold' }}>Slot Duration</TableCell>
              <TableCell sx={{ fontWeight: 'bold' }}>Status</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {isScheduleLoading ? (
              <TableRow>
                <TableCell colSpan={5} align="center" sx={{ py: 3 }}>
                  <CircularProgress size={30} />
                </TableCell>
              </TableRow>
            ) : schedule && schedule.length > 0 ? (
              schedule.map((row) => (
                <TableRow key={row.id} hover>
                  <TableCell>{row.dayOfWeek}</TableCell>
                  <TableCell>{row.startTime}</TableCell>
                  <TableCell>{row.endTime}</TableCell>
                  <TableCell>{row.slotDurationMinutes} mins</TableCell>
                  <TableCell>
                    <Chip 
                      label={row.active ? 'Active' : 'Inactive'} 
                      color={row.active ? 'success' : 'default'} 
                      size="small"
                    />
                  </TableCell>
                </TableRow>
              ))
            ) : (
              <TableRow>
                <TableCell colSpan={5} align="center" sx={{ py: 4 }}>
                  <Typography variant="body1" color="text.secondary">
                    No availability schedule found. Add slots to get started.
                  </Typography>
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
  );
};

export default AvailabilityPage;
