import React from 'react';
import { Grid } from '@mui/material';
import StatCard from '../../../components/StatCard';
import EventIcon from '@mui/icons-material/Event';
import EventAvailableIcon from '@mui/icons-material/EventAvailable';
import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline';
import CancelOutlinedIcon from '@mui/icons-material/CancelOutlined';
import TodayIcon from '@mui/icons-material/Today';
import { DoctorDashboardStats } from '../types/doctorTypes';

interface DashboardStatsProps {
  stats?: DoctorDashboardStats;
}

const DashboardStats: React.FC<DashboardStatsProps> = ({ stats }) => {
  if (!stats) return null;

  return (
    <Grid container spacing={3} sx={{ mb: 4 }}>
      <Grid item xs={12} sm={6} md={4} lg={2.4}>
        <StatCard title="Total" value={stats.totalAppointments} icon={<EventIcon />} color="primary" />
      </Grid>
      <Grid item xs={12} sm={6} md={4} lg={2.4}>
        <StatCard title="Today" value={stats.todayAppointments} icon={<TodayIcon />} color="info" />
      </Grid>
      <Grid item xs={12} sm={6} md={4} lg={2.4}>
        <StatCard title="Upcoming" value={stats.upcomingAppointments} icon={<EventAvailableIcon />} color="warning" />
      </Grid>
      <Grid item xs={12} sm={6} md={4} lg={2.4}>
        <StatCard title="Completed" value={stats.completedAppointments} icon={<CheckCircleOutlineIcon />} color="success" />
      </Grid>
      <Grid item xs={12} sm={6} md={4} lg={2.4}>
        <StatCard title="Cancelled" value={stats.cancelledAppointments} icon={<CancelOutlinedIcon />} color="error" />
      </Grid>
    </Grid>
  );
};

export default DashboardStats;
