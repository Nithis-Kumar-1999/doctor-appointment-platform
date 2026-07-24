import React, { useState, useMemo } from 'react';
import { Box, Typography, Paper, Button, Chip, Divider, Alert, CircularProgress, TextField } from '@mui/material';
import Grid from '@mui/material/Grid';
import { useLocation, useNavigate } from 'react-router-dom';
import { DoctorResponse } from '../../doctor/types/doctor.types';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { useDoctorSchedule } from '../../doctor/hooks/useAvailability';
import { useBookAppointment } from '../../appointment/hooks/useAppointments';
import { generateTimeSlots, getDayOfWeek } from '../../appointment/utils/timeSlotUtils';
import { SlotPicker } from '../../appointment/components/SlotPicker';
import { ConfirmationDialog } from '../../../components/ConfirmationDialog';

const DoctorDetailsPage: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();
  
  const doctor = location.state?.doctor as DoctorResponse;

  const [selectedDate, setSelectedDate] = useState<string>('');
  const [selectedSlot, setSelectedSlot] = useState<string | null>(null);
  const [reason, setReason] = useState<string>('');
  const [confirmOpen, setConfirmOpen] = useState(false);

  const { data: schedule, isLoading: isLoadingSchedule } = useDoctorSchedule(doctor?.id);
  const bookMutation = useBookAppointment();

  // Find the availability config for the selected date's day of week
  const availabilityForDate = useMemo(() => {
    if (!selectedDate || !schedule) return undefined;
    const dayOfWeek = getDayOfWeek(selectedDate);
    return schedule.find(s => s.dayOfWeek === dayOfWeek);
  }, [selectedDate, schedule]);

  // Generate slots for the selected date
  const availableSlots = useMemo(() => {
    return generateTimeSlots(availabilityForDate);
  }, [availabilityForDate]);

  const handleBook = () => {
    if (!selectedDate || !selectedSlot || !reason) return;
    setConfirmOpen(true);
  };

  const executeBooking = () => {
    bookMutation.mutate(
      {
        doctorId: doctor.id,
        appointmentDate: selectedDate,
        appointmentTime: selectedSlot!,
        reason: reason
      },
      {
        onSuccess: () => {
          setConfirmOpen(false);
          // Redirect to patient dashboard or appointments list
          navigate('/patient/appointments');
        },
        onError: () => {
          setConfirmOpen(false);
        }
      }
    );
  };

  if (!doctor) {
    return (
      <Box sx={{ p: 4, maxWidth: 800, mx: 'auto', textAlign: 'center' }}>
        <Alert severity="warning" sx={{ mb: 3 }}>
          Doctor details not found. Please select a doctor from the search page.
        </Alert>
        <Button startIcon={<ArrowBackIcon />} onClick={() => navigate('/patient/search')}>
          Back to Search
        </Button>
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3, maxWidth: 1000, mx: 'auto' }}>
      <Button 
        startIcon={<ArrowBackIcon />} 
        onClick={() => navigate(-1)}
        sx={{ mb: 3 }}
      >
        Back
      </Button>

      <Grid container spacing={4}>
        <Grid size={{ xs: 12, md: 7 }}>
          <Paper elevation={3} sx={{ p: 4, borderRadius: 2, height: '100%' }}>
            <Typography variant="h3" sx={{ fontWeight: 'bold', mb: 1 }}>
              Dr. {doctor.firstName} {doctor.lastName}
            </Typography>
            
            <Chip 
              label={doctor.specialty.replace('_', ' ')} 
              color="primary" 
              sx={{ fontSize: '1rem', mb: 3 }} 
            />

            <Typography variant="h6" gutterBottom>Professional Info</Typography>
            <Divider sx={{ mb: 2 }} />
            
            <Typography variant="body1" sx={{ mb: 1.5 }}>
              <strong>Qualification:</strong> {doctor.qualification}
            </Typography>
            
            <Typography variant="body1" sx={{ mb: 1.5 }}>
              <strong>Experience:</strong> {doctor.experienceYears} years
            </Typography>
            
            <Typography variant="body1" sx={{ mb: 3 }}>
              <strong>Consultation Fee:</strong> ${doctor.consultationFee}
            </Typography>

            <Typography variant="h6" gutterBottom>Biography</Typography>
            <Divider sx={{ mb: 2 }} />
            <Typography variant="body1" color="text.secondary" sx={{ whiteSpace: 'pre-line' }}>
              No biography available for this doctor.
            </Typography>
          </Paper>
        </Grid>
        
        <Grid size={{ xs: 12, md: 5 }}>
          <Paper elevation={3} sx={{ p: 4, borderRadius: 2, height: '100%', display: 'flex', flexDirection: 'column' }}>
            <Typography variant="h5" sx={{ fontWeight: 'bold', mb: 3 }}>
              Book Appointment
            </Typography>

            <Box sx={{ mb: 3 }}>
              <TextField
                fullWidth
                type="date"
                label="Select Date"
                required
                slotProps={{ 
                  inputLabel: { shrink: true },
                  htmlInput: { min: new Date().toISOString().split('T')[0] }
                }}
                value={selectedDate}
                onChange={(e) => {
                  setSelectedDate(e.target.value);
                  setSelectedSlot(null);
                }}
              />
            </Box>

            {selectedDate && (
              <Box sx={{ mb: 3, flexGrow: 1 }}>
                <Typography variant="subtitle2" sx={{ mb: 1.5, fontWeight: 'bold' }}>
                  Available Time Slots
                </Typography>
                
                {isLoadingSchedule ? (
                  <CircularProgress size={24} />
                ) : !availabilityForDate ? (
                  <Alert severity="info" sx={{ py: 0 }}>
                    Doctor is not available on {getDayOfWeek(selectedDate)}.
                  </Alert>
                ) : (
                  <SlotPicker 
                    slots={availableSlots}
                    selectedSlot={selectedSlot}
                    onSelectSlot={setSelectedSlot}
                  />
                )}
              </Box>
            )}

            <Box sx={{ mb: 3 }}>
              <TextField
                fullWidth
                multiline
                rows={2}
                label="Reason for Visit"
                required
                value={reason}
                onChange={(e) => setReason(e.target.value)}
              />
            </Box>

            <Button 
              variant="contained" 
              size="large" 
              fullWidth
              disabled={!selectedDate || !selectedSlot || !reason || bookMutation.isPending}
              onClick={handleBook}
            >
              {bookMutation.isPending ? <CircularProgress size={24} /> : `Book for $${doctor.consultationFee}`}
            </Button>
          </Paper>
        </Grid>
      </Grid>

      <ConfirmationDialog 
        open={confirmOpen}
        title="Confirm Appointment"
        message={`Are you sure you want to book an appointment with Dr. ${doctor.lastName} on ${selectedDate} at ${selectedSlot?.substring(0, 5)}?`}
        confirmText="Yes, Book it"
        onConfirm={executeBooking}
        onCancel={() => setConfirmOpen(false)}
        isLoading={bookMutation.isPending}
      />
    </Box>
  );
};

export default DoctorDetailsPage;
