import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import PageContainer from '../../../components/PageContainer';
import PageHeader from '../../../components/PageHeader';
import AppointmentBookingWizard from '../components/AppointmentBookingWizard';
import { Button } from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';

const BookAppointmentPage = () => {
  const { doctorId } = useParams<{ doctorId: string }>();
  const navigate = useNavigate();

  if (!doctorId || isNaN(Number(doctorId))) {
    return (
      <PageContainer>
        <PageHeader 
          title="Invalid Doctor" 
          subtitle="Cannot proceed with booking." 
          action={<Button startIcon={<ArrowBackIcon />} onClick={() => navigate(-1)}>Go Back</Button>}
        />
      </PageContainer>
    );
  }

  return (
    <PageContainer>
      <PageHeader 
        title="Book Appointment" 
        subtitle="Follow the steps below to secure your consultation."
        action={<Button startIcon={<ArrowBackIcon />} onClick={() => navigate(-1)}>Back</Button>}
      />
      
      <AppointmentBookingWizard doctorId={Number(doctorId)} />
    </PageContainer>
  );
};

export default BookAppointmentPage;
