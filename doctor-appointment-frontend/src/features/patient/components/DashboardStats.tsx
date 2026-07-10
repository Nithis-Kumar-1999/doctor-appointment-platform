import React from 'react';
import { Grid } from '@mui/material';
import StatCard from '../../../components/StatCard';
import EventAvailableIcon from '@mui/icons-material/EventAvailable';
import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline';
import CancelOutlinedIcon from '@mui/icons-material/CancelOutlined';
import { PatientDashboardStats } from '../types/patientTypes';

interface DashboardStatsProps {
  stats?: PatientDashboardStats;
}

const DashboardStats: React.FC<DashboardStatsProps> = ({ stats }) => {
  if (!stats) return null;

  return (
    <Grid container spacing={3} sx={{ mb: 4 }}>
      <Grid item xs={12} sm={4}>
        <StatCard title="Upcoming" value={stats.upcomingAppointments} icon={<EventAvailableIcon />} color="info" />
      </Grid>
      <Grid item xs={12} sm={4}>
        <StatCard title="Completed" value={stats.completedAppointments} icon={<CheckCircleOutlineIcon />} color="success" />
      </Grid>
      <Grid item xs={12} sm={4}>
        <StatCard title="Cancelled" value={stats.cancelledAppointments} icon={<CancelOutlinedIcon />} color="error" />
      </Grid>
    </Grid>
  );
};

export default DashboardStats;
