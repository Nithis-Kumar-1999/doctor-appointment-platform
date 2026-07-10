import React from 'react';
import { Card, CardContent, Typography, Box, Avatar, Button, Rating, Divider } from '@mui/material';
import CalendarMonthIcon from '@mui/icons-material/CalendarMonth';
import { useNavigate } from 'react-router-dom';
import { DoctorProfile } from '../../doctor/types/doctorTypes';

interface DoctorSearchCardProps {
  doctor: DoctorProfile;
}

const DoctorSearchCard: React.FC<DoctorSearchCardProps> = ({ doctor }) => {
  const navigate = useNavigate();

  return (
    <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column', transition: '0.3s', '&:hover': { transform: 'translateY(-4px)', boxShadow: 4 } }}>
      <CardContent sx={{ flexGrow: 1 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 2 }}>
          <Avatar sx={{ width: 60, height: 60, bgcolor: 'primary.light' }}>
            {doctor.specialty.charAt(0)}
          </Avatar>
          <Box>
            <Typography variant="h6" fontWeight="bold">Dr. {doctor.userId || 'Unknown'}</Typography>
            <Typography variant="body2" color="primary.main" fontWeight="medium">{doctor.specialty}</Typography>
          </Box>
        </Box>
        
        <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
          <Rating value={4.5} readOnly size="small" precision={0.5} />
          <Typography variant="body2" color="text.secondary" sx={{ ml: 1 }}>(124 reviews)</Typography>
        </Box>

        <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
          <strong>Experience:</strong> {doctor.experienceYears} Years
        </Typography>
        <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
          <strong>Consultation Fee:</strong> ${doctor.consultationFee}
        </Typography>
        
        <Divider sx={{ mb: 2 }} />
        
        <Box sx={{ display: 'flex', alignItems: 'center', color: 'success.main', mb: 2 }}>
          <CalendarMonthIcon fontSize="small" sx={{ mr: 1 }} />
          <Typography variant="body2" fontWeight="bold">Next slot: Tomorrow, 10:00 AM</Typography>
        </Box>

        <Box sx={{ display: 'flex', gap: 1 }}>
          <Button variant="outlined" fullWidth size="small">View Profile</Button>
          <Button variant="contained" fullWidth size="small" onClick={() => navigate(`/patient/book-appointment/${doctor.userId || 1}`)}>Book Slot</Button>
        </Box>
      </CardContent>
    </Card>
  );
};

export default React.memo(DoctorSearchCard);
