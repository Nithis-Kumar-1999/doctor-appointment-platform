import React from 'react';
import PageContainer from '../../../components/PageContainer';
import PageHeader from '../../../components/PageHeader';
import DashboardStats from '../components/DashboardStats';
import AppointmentHistoryTable from '../components/AppointmentHistoryTable';
import { usePatientDashboardStats } from '../hooks/usePatientQueries';
import LoadingOverlay from '../../../components/LoadingOverlay';
import InfoCard from '../../../components/InfoCard';
import { Grid, Button } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import SearchIcon from '@mui/icons-material/Search';

const PatientDashboard = () => {
  const { data: stats, isLoading } = usePatientDashboardStats();
  const navigate = useNavigate();

  return (
    <PageContainer>
      <PageHeader 
        title="Patient Dashboard" 
        subtitle="Manage your health and upcoming appointments."
        breadcrumbs={[{ label: 'Dashboard' }]}
        action={<Button variant="contained" startIcon={<SearchIcon />} onClick={() => navigate('/patient/find-doctors')}>Find a Doctor</Button>}
      />
      
      {isLoading ? (
        <LoadingOverlay open={true} message="Loading dashboard..." />
      ) : (
        <>
          <DashboardStats stats={stats} />

          <Grid container spacing={3} sx={{ mb: 4 }}>
            <Grid item xs={12} md={6}>
              <InfoCard 
                title="Health Summary" 
                details={[
                  { label: 'Blood Group', value: 'O+' },
                  { label: 'Allergies', value: 'Penicillin' },
                  { label: 'Height', value: '175 cm' },
                  { label: 'Weight', value: '70 kg' }
                ]} 
              />
            </Grid>
            <Grid item xs={12} md={6}>
              <InfoCard 
                title="Favorite Doctor" 
                details={[
                  { label: 'Name', value: 'Dr. John Doe' },
                  { label: 'Specialty', value: 'Cardiology' },
                  { label: 'Last Visit', value: '2026-05-10' }
                ]} 
                action={<Button size="small">Book Again</Button>}
              />
            </Grid>
          </Grid>
          
          <PageHeader title="Recent Appointments" />
          <AppointmentHistoryTable />
        </>
      )}
    </PageContainer>
  );
};

export default PatientDashboard;
