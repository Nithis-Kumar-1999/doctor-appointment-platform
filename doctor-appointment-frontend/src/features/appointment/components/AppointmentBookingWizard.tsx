import React, { useState } from 'react';
import { Box, Stepper, Step, StepLabel, Button, Typography, TextField } from '@mui/material';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { appointmentBookingSchema, AppointmentBookingFormValues } from '../utils/appointmentSchema';
import DoctorInfoCard from './DoctorInfoCard';
import CalendarView from './CalendarView';
import TimeSlotSelector from './TimeSlotSelector';
import BookingSummary from './BookingSummary';
import { useAvailableSlots, useBookAppointment } from '../hooks/useAppointmentQueries';
import AppointmentConfirmationDialog from './AppointmentConfirmationDialog';
import { useAuth } from '../../../context/AuthContext';

const steps = ['Select Date & Time', 'Reason for Visit', 'Confirm Booking'];

interface WizardProps {
  doctorId: number;
}

const AppointmentBookingWizard: React.FC<WizardProps> = ({ doctorId }) => {
  const [activeStep, setActiveStep] = useState(0);
  const [confirmOpen, setConfirmOpen] = useState(false);
  const { user } = useAuth(); // Assuming patientId is user.id
  const bookMutation = useBookAppointment();

  const { control, handleSubmit, watch, trigger, setValue, formState: { errors } } = useForm<AppointmentBookingFormValues>({
    resolver: zodResolver(appointmentBookingSchema),
    defaultValues: { doctorId, appointmentDate: '', appointmentTime: '', reason: '' }
  });

  const selectedDate = watch('appointmentDate');
  const selectedTime = watch('appointmentTime');
  const reason = watch('reason');

  const { data: availableSlots, isLoading: slotsLoading } = useAvailableSlots(doctorId, selectedDate);

  const handleNext = async () => {
    let isStepValid = true;
    if (activeStep === 0) {
      isStepValid = await trigger(['appointmentDate', 'appointmentTime']);
    } else if (activeStep === 1) {
      isStepValid = await trigger(['reason']);
    }
    
    if (isStepValid) setActiveStep((prev) => prev + 1);
  };

  const handleBack = () => setActiveStep((prev) => prev - 1);

  const onSubmit = () => setConfirmOpen(true);

  const executeBooking = () => {
    // For demo purposes, fallback to patientId = 1 if user context doesn't map directly
    const patientId = user?.id || 1; 
    bookMutation.mutate({
      doctorId,
      patientId,
      appointmentDate: selectedDate,
      appointmentTime: `${selectedTime}:00`, // append seconds
      reason
    });
  };

  return (
    <Box sx={{ width: '100%', mt: 4 }}>
      <DoctorInfoCard doctorId={doctorId} />

      <Stepper activeStep={activeStep} alternativeLabel sx={{ mb: 4 }}>
        {steps.map((label) => (
          <Step key={label}><StepLabel>{label}</StepLabel></Step>
        ))}
      </Stepper>

      <Box sx={{ minHeight: 300, p: 2 }}>
        {activeStep === 0 && (
          <Box>
            <Controller
              name="appointmentDate"
              control={control}
              render={({ field }) => (
                <CalendarView 
                  value={field.value} 
                  onChange={(date) => { field.onChange(date); setValue('appointmentTime', ''); }}
                  error={!!errors.appointmentDate}
                  helperText={errors.appointmentDate?.message}
                />
              )}
            />
            {selectedDate && (
              <Box sx={{ mt: 4 }}>
                <Typography variant="subtitle1" align="center" gutterBottom>Available Time Slots</Typography>
                <Controller
                  name="appointmentTime"
                  control={control}
                  render={({ field }) => (
                    <TimeSlotSelector 
                      slots={availableSlots || []} 
                      selectedSlot={field.value} 
                      onSelectSlot={field.onChange} 
                      isLoading={slotsLoading}
                      error={!!errors.appointmentTime}
                      helperText={errors.appointmentTime?.message}
                    />
                  )}
                />
              </Box>
            )}
          </Box>
        )}

        {activeStep === 1 && (
          <Box sx={{ maxWidth: 500, mx: 'auto' }}>
            <Typography variant="h6" gutterBottom>Why are you visiting?</Typography>
            <Controller
              name="reason"
              control={control}
              render={({ field }) => (
                <TextField 
                  {...field} 
                  fullWidth 
                  multiline 
                  rows={4} 
                  placeholder="E.g., Having persistent headaches..." 
                  error={!!errors.reason} 
                  helperText={errors.reason?.message} 
                />
              )}
            />
          </Box>
        )}

        {activeStep === 2 && (
          <BookingSummary date={selectedDate} time={selectedTime} reason={reason} />
        )}
      </Box>

      <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 4 }}>
        <Button disabled={activeStep === 0} onClick={handleBack}>Back</Button>
        {activeStep === steps.length - 1 ? (
          <Button variant="contained" color="primary" onClick={handleSubmit(onSubmit)} disabled={bookMutation.isPending}>
            Confirm Booking
          </Button>
        ) : (
          <Button variant="contained" onClick={handleNext}>Next</Button>
        )}
      </Box>

      <AppointmentConfirmationDialog 
        open={confirmOpen} 
        onConfirm={executeBooking} 
        onCancel={() => setConfirmOpen(false)} 
        isLoading={bookMutation.isPending} 
      />
    </Box>
  );
};

export default AppointmentBookingWizard;
