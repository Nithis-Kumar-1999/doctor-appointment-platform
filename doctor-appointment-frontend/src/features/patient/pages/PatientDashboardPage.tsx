import React from 'react';
import { Box, Grid, Typography, Card, CardContent, CardActionArea, CircularProgress } from '@mui/material';
import PersonIcon from '@mui/icons-material/Person';
import SearchIcon from '@mui/icons-material/Search';
import EventIcon from '@mui/icons-material/Event';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../../context/AuthContext';
import { usePatientProfile } from '../hooks/usePatientProfile';

import { usePatientAppointments } from '../../appointment/hooks/useAppointments';
import { AppointmentStatus } from '../../appointment/types/appointment.types';

const PatientDashboardPage: React.FC = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const { data: profile, isLoading: isLoadingProfile } = usePatientProfile();
  
  // Fetch pending/confirmed appointments for stats
  const { data: upcomingApts } = usePatientAppointments(AppointmentStatus.CONFIRMED, 0, 1);
  // We only need upcoming for the stat, removing completedApts to satisfy linter

  const totalUpcoming = (upcomingApts?.totalElements || 0);

  const summaryCards = [
    {
      title: 'Search Doctors',
      value: 'Find & Book',
      icon: <SearchIcon sx={{ fontSize: 40, color: 'primary.main' }} />,
      color: '#e3f2fd',
      action: () => navigate('/patient/search'),
    },
    {
      title: 'My Profile',
      value: profile ? 'Manage Profile' : 'Setup Profile',
      icon: <PersonIcon sx={{ fontSize: 40, color: 'secondary.main' }} />,
      color: '#f3e5f5',
      action: () => navigate('/patient/profile'),
    },
    {
      title: 'Appointments',
      value: `${totalUpcoming} Upcoming`,
      icon: <EventIcon sx={{ fontSize: 40, color: 'success.main' }} />,
      color: '#e8f5e9',
      action: () => navigate('/patient/appointments'),
    },
  ];

  if (isLoadingProfile) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', p: 5 }}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3, maxWidth: 1200, mx: 'auto' }}>
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" sx={{ fontWeight: 'bold', mb: 1 }}>
          Welcome back, {user?.firstName}!
        </Typography>
        <Typography variant="body1" color="text.secondary">
          {profile 
            ? 'Here is an overview of your healthcare portal.' 
            : 'Please complete your profile setup to start booking appointments.'}
        </Typography>
      </Box>

      <Grid container spacing={3}>
        {summaryCards.map((card, index) => (
          <Grid size={{ xs: 12, sm: 6, md: 4 }} key={index}>
            <Card
              elevation={2}
              sx={{
                height: '100%',
                display: 'flex',
                flexDirection: 'column',
                transition: 'transform 0.2s',
                '&:hover': { transform: 'translateY(-4px)', boxShadow: 4 }
              }}
            >
              <CardActionArea onClick={card.action} sx={{ height: '100%', p: 2 }}>
                <CardContent sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                  <Box>
                    <Typography color="text.secondary" gutterBottom variant="h6">
                      {card.title}
                    </Typography>
                    <Typography variant="h5" sx={{ fontWeight: 'bold' }}>
                      {card.value}
                    </Typography>
                  </Box>
                  <Box
                    sx={{
                      backgroundColor: card.color,
                      p: 2,
                      borderRadius: '50%',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                    }}
                  >
                    {card.icon}
                  </Box>
                </CardContent>
              </CardActionArea>
            </Card>
          </Grid>
        ))}
      </Grid>
    </Box>
  );
};

export default PatientDashboardPage;
