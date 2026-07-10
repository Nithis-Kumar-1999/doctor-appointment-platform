import React from 'react';
import { Box, Typography, Divider, Paper } from '@mui/material';

interface BookingSummaryProps {
  date: string;
  time: string;
  reason: string;
}

const BookingSummary: React.FC<BookingSummaryProps> = ({ date, time, reason }) => {
  return (
    <Paper variant="outlined" sx={{ p: 3, maxWidth: 500, mx: 'auto' }}>
      <Typography variant="h6" fontWeight="bold" gutterBottom align="center">
        Appointment Summary
      </Typography>
      <Divider sx={{ mb: 2 }} />
      
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
        <Typography color="text.secondary">Date</Typography>
        <Typography fontWeight="bold">{date}</Typography>
      </Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
        <Typography color="text.secondary">Time</Typography>
        <Typography fontWeight="bold">{time}</Typography>
      </Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
        <Typography color="text.secondary">Consultation Fee</Typography>
        <Typography fontWeight="bold">$150</Typography>
      </Box>
      <Divider sx={{ my: 2 }} />
      <Box>
        <Typography color="text.secondary" gutterBottom>Reason for Visit</Typography>
        <Typography variant="body2">{reason || 'Not provided'}</Typography>
      </Box>
    </Paper>
  );
};

export default BookingSummary;
