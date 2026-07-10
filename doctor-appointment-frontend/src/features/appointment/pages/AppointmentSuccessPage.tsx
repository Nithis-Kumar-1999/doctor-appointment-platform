import React from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { Box, Card, Typography, Button } from '@mui/material';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import PageContainer from '../../../components/PageContainer';

const AppointmentSuccessPage = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  return (
    <PageContainer>
      <Box sx={{ display: 'flex', justifyContent: 'center', mt: 8 }}>
        <Card sx={{ maxWidth: 500, width: '100%', textAlign: 'center', p: 4 }}>
          <CheckCircleIcon color="success" sx={{ fontSize: 80, mb: 2 }} />
          <Typography variant="h4" fontWeight="bold" gutterBottom>
            Booking Confirmed!
          </Typography>
          <Typography variant="body1" color="text.secondary" paragraph>
            Your appointment has been successfully booked. You can view the details in your appointments dashboard.
          </Typography>
          
          <Box sx={{ my: 3, p: 2, bgcolor: 'background.default', borderRadius: 2 }}>
            <Typography variant="subtitle2" color="text.secondary">Appointment ID</Typography>
            <Typography variant="h6" fontWeight="bold">#{id}</Typography>
          </Box>

          <Box sx={{ display: 'flex', gap: 2, justifyContent: 'center', mt: 4 }}>
            <Button variant="outlined" onClick={() => navigate('/patient/dashboard')}>
              Go to Dashboard
            </Button>
            <Button variant="contained" component={Link} to={`/patient/appointment-details/${id}`}>
              View Details
            </Button>
          </Box>
        </Card>
      </Box>
    </PageContainer>
  );
};

export default AppointmentSuccessPage;
