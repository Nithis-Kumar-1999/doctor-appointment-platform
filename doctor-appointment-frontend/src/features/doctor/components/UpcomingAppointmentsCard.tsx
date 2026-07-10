import React from 'react';
import { Card, CardContent, Typography, Box } from '@mui/material';
import AppointmentTable from './AppointmentTable';

const UpcomingAppointmentsCard = () => {
  return (
    <Card sx={{ mb: 4 }}>
      <CardContent>
        <Box sx={{ mb: 3 }}>
          <Typography variant="h6" fontWeight="bold">
            Recent & Upcoming Appointments
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Manage your patient schedule and update statuses.
          </Typography>
        </Box>
        {/* We reuse the generic AppointmentTable here, potentially pre-filtered in a real app */}
        <AppointmentTable />
      </CardContent>
    </Card>
  );
};

export default UpcomingAppointmentsCard;
