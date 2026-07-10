import React from 'react';
import { Card, CardContent, Typography, Box, Avatar } from '@mui/material';

interface DoctorInfoCardProps {
  doctorId: number;
}

const DoctorInfoCard: React.FC<DoctorInfoCardProps> = ({ doctorId }) => {
  // In a real app, you'd fetch the doctor's details here or pass them as props.
  // Using placeholder data for wizard demo
  return (
    <Card variant="outlined" sx={{ mb: 3 }}>
      <CardContent sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
        <Avatar sx={{ width: 64, height: 64, bgcolor: 'primary.main' }}>D</Avatar>
        <Box>
          <Typography variant="h6" fontWeight="bold">Dr. Placeholder {doctorId}</Typography>
          <Typography variant="body2" color="primary">Cardiology</Typography>
          <Typography variant="body2" color="text.secondary">Consultation Fee: $150</Typography>
        </Box>
      </CardContent>
    </Card>
  );
};

export default DoctorInfoCard;
