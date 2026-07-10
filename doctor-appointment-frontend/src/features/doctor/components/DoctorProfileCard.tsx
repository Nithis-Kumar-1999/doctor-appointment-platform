import React from 'react';
import { Card, CardContent, Typography, Box, Avatar, Button, Skeleton } from '@mui/material';
import { DoctorProfile } from '../types/doctorTypes';
import EditIcon from '@mui/icons-material/Edit';

interface DoctorProfileCardProps {
  profile?: DoctorProfile;
  isLoading: boolean;
  onEditClick: () => void;
}

const DoctorProfileCard: React.FC<DoctorProfileCardProps> = ({ profile, isLoading, onEditClick }) => {
  if (isLoading) {
    return <Skeleton variant="rectangular" height={250} sx={{ borderRadius: 2 }} />;
  }

  if (!profile) return null;

  return (
    <Card sx={{ mb: 4 }}>
      <CardContent sx={{ display: 'flex', flexDirection: { xs: 'column', sm: 'row' }, alignItems: { xs: 'center', sm: 'flex-start' }, gap: 4 }}>
        <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
          <Avatar sx={{ width: 120, height: 120, mb: 2, fontSize: '3rem', bgcolor: 'primary.main' }}>
            {profile.specialty.charAt(0)}
          </Avatar>
          <Button variant="outlined" size="small" aria-label="Upload profile picture">Change Photo</Button>
        </Box>
        <Box sx={{ flexGrow: 1, textAlign: { xs: 'center', sm: 'left' } }}>
          <Typography variant="h4" fontWeight="bold" gutterBottom>
            Doctor Profile
          </Typography>
          <Typography variant="h6" color="primary.main" gutterBottom>
            {profile.specialty}
          </Typography>
          <Typography variant="body1" color="text.secondary" paragraph>
            {profile.bio || 'No biography provided.'}
          </Typography>
          <Box sx={{ display: 'flex', gap: 4, flexWrap: 'wrap', justifyContent: { xs: 'center', sm: 'flex-start' } }}>
            <Box>
              <Typography variant="subtitle2" color="text.secondary">Experience</Typography>
              <Typography variant="body1" fontWeight="bold">{profile.experienceYears} Years</Typography>
            </Box>
            <Box>
              <Typography variant="subtitle2" color="text.secondary">Consultation Fee</Typography>
              <Typography variant="body1" fontWeight="bold">${profile.consultationFee}</Typography>
            </Box>
          </Box>
        </Box>
        <Box>
          <Button variant="contained" startIcon={<EditIcon />} onClick={onEditClick}>
            Edit Profile
          </Button>
        </Box>
      </CardContent>
    </Card>
  );
};

export default DoctorProfileCard;
