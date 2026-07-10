import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Box, Card, CardContent, Typography, Button, CircularProgress, Divider, Grid } from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import PageContainer from '../../../components/PageContainer';
import PageHeader from '../../../components/PageHeader';
import AppointmentStatusTimeline from '../components/AppointmentStatusTimeline';
import { useAppointmentDetails } from '../hooks/useAppointmentQueries';

const AppointmentDetailsPage = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { data: appointment, isLoading, isError } = useAppointmentDetails(Number(id));

  if (isLoading) return <PageContainer><Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}><CircularProgress /></Box></PageContainer>;
  
  if (isError || !appointment) {
    return <PageContainer><Typography color="error">Failed to load appointment details.</Typography></PageContainer>;
  }

  return (
    <PageContainer>
      <PageHeader 
        title="Appointment Details" 
        subtitle={`ID: #${appointment.id}`}
        action={<Button startIcon={<ArrowBackIcon />} onClick={() => navigate(-1)}>Back</Button>}
      />

      <Card sx={{ mb: 4 }}>
        <CardContent>
          <AppointmentStatusTimeline status={appointment.status} />
          
          <Divider sx={{ my: 3 }} />
          
          <Grid container spacing={4}>
            <Grid item xs={12} md={6}>
              <Typography variant="h6" fontWeight="bold" gutterBottom>Schedule</Typography>
              <Box sx={{ mb: 2 }}>
                <Typography variant="body2" color="text.secondary">Date</Typography>
                <Typography variant="body1">{appointment.appointmentDate}</Typography>
              </Box>
              <Box sx={{ mb: 2 }}>
                <Typography variant="body2" color="text.secondary">Time</Typography>
                <Typography variant="body1">{appointment.appointmentTime}</Typography>
              </Box>
              <Box sx={{ mb: 2 }}>
                <Typography variant="body2" color="text.secondary">Created On</Typography>
                <Typography variant="body1">{new Date(appointment.createdAt || '').toLocaleString()}</Typography>
              </Box>
            </Grid>
            <Grid item xs={12} md={6}>
              <Typography variant="h6" fontWeight="bold" gutterBottom>Consultation Info</Typography>
              <Box sx={{ mb: 2 }}>
                <Typography variant="body2" color="text.secondary">Doctor</Typography>
                <Typography variant="body1">{appointment.doctorName}</Typography>
              </Box>
              <Box sx={{ mb: 2 }}>
                <Typography variant="body2" color="text.secondary">Specialty</Typography>
                <Typography variant="body1">{appointment.specialty || 'General'}</Typography>
              </Box>
              <Box sx={{ mb: 2 }}>
                <Typography variant="body2" color="text.secondary">Fee</Typography>
                <Typography variant="body1">${appointment.consultationFee}</Typography>
              </Box>
            </Grid>
          </Grid>
          
          <Divider sx={{ my: 3 }} />
          
          <Box>
            <Typography variant="h6" fontWeight="bold" gutterBottom>Reason for Visit</Typography>
            <Typography variant="body1" sx={{ bgcolor: 'background.default', p: 2, borderRadius: 1 }}>
              {appointment.reason || 'No reason provided.'}
            </Typography>
          </Box>
        </CardContent>
      </Card>
      
      {/* Patient can only cancel if scheduled/pending */}
      {['SCHEDULED', 'PENDING'].includes(appointment.status) && (
        <Box sx={{ display: 'flex', justifyContent: 'flex-end' }}>
          <Button variant="outlined" color="error">Cancel Appointment</Button>
        </Box>
      )}
    </PageContainer>
  );
};

export default AppointmentDetailsPage;
