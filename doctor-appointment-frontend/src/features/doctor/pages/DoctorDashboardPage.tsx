import React from 'react';
import { Box, Typography, Grid, Paper } from '@mui/material';
import EventIcon from '@mui/icons-material/Event';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import AccountCircleIcon from '@mui/icons-material/AccountCircle';
import { useAuth } from '../../../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import { useDoctorAppointments } from '../../appointment/hooks/useAppointments';
import { AppointmentStatus } from '../../appointment/types/appointment.types';

const DoctorDashboardPage: React.FC = () => {
  const { user } = useAuth();
  const navigate = useNavigate();

  const { data: upcomingApts } = useDoctorAppointments(AppointmentStatus.CONFIRMED, 0, 1);
  const { data: pendingApts } = useDoctorAppointments(AppointmentStatus.PENDING, 0, 1);
  // removed completedApts for linter

  const totalUpcoming = (upcomingApts?.totalElements || 0) + (pendingApts?.totalElements || 0);

  const summaryCards = [
    {
      title: 'Appointments',
      value: `${totalUpcoming} Upcoming`,
      icon: <EventIcon sx={{ fontSize: 40, color: 'primary.main' }} />,
      onClick: () => navigate('/doctor/appointments'),
    },
    {
      title: 'Weekly Schedule',
      value: 'Availability',
      icon: <AccessTimeIcon sx={{ fontSize: 40, color: 'secondary.main' }} />,
      onClick: () => navigate('/doctor/availability'),
    },
    {
      title: 'My Profile',
      value: 'Manage Details',
      icon: <AccountCircleIcon sx={{ fontSize: 40, color: 'success.main' }} />,
      onClick: () => navigate('/doctor/profile'),
    },
  ];

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" gutterBottom sx={{ fontWeight: 'bold' }}>
        Welcome, {user?.firstName || 'Doctor'}
      </Typography>
      <Typography variant="subtitle1" color="text.secondary" sx={{ mb: 4 }}>
        Here is your quick overview for today.
      </Typography>

      <Grid container spacing={3}>
        {summaryCards.map((card, index) => (
          <Grid size={{ xs: 12, sm: 6, md: 4 }} key={index}>
            <Paper
              elevation={2}
              sx={{
                p: 3,
                display: 'flex',
                alignItems: 'center',
                cursor: 'pointer',
                transition: 'transform 0.2s',
                '&:hover': {
                  transform: 'translateY(-4px)',
                  boxShadow: 4,
                },
              }}
              onClick={card.onClick}
            >
              <Box sx={{ mr: 2 }}>{card.icon}</Box>
              <Box>
                <Typography variant="h6">{card.title}</Typography>
                <Typography variant="body2" color="text.secondary">
                  {card.value}
                </Typography>
              </Box>
            </Paper>
          </Grid>
        ))}
      </Grid>
    </Box>
  );
};

export default DoctorDashboardPage;
