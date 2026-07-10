import React from 'react';
import { Card, CardContent, Typography, Box, Avatar, Button, Skeleton } from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import { PatientProfile } from '../types/patientTypes';

interface PatientProfileCardProps {
  profile?: PatientProfile;
  isLoading: boolean;
  onEditClick: () => void;
}

const PatientProfileCard: React.FC<PatientProfileCardProps> = ({ profile, isLoading, onEditClick }) => {
  if (isLoading) {
    return <Skeleton variant="rectangular" height={200} sx={{ borderRadius: 2 }} />;
  }

  if (!profile) return null;

  return (
    <Card sx={{ mb: 4 }}>
      <CardContent sx={{ display: 'flex', flexDirection: { xs: 'column', sm: 'row' }, alignItems: { xs: 'center', sm: 'flex-start' }, gap: 4 }}>
        <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
          <Avatar sx={{ width: 100, height: 100, mb: 2, bgcolor: 'secondary.main' }}>
            {profile.gender === 'MALE' ? 'M' : profile.gender === 'FEMALE' ? 'F' : 'O'}
          </Avatar>
          <Button variant="outlined" size="small">Change Photo</Button>
        </Box>
        <Box sx={{ flexGrow: 1, textAlign: { xs: 'center', sm: 'left' } }}>
          <Typography variant="h4" fontWeight="bold" gutterBottom>
            My Profile
          </Typography>
          <Box sx={{ display: 'flex', gap: 4, flexWrap: 'wrap', justifyContent: { xs: 'center', sm: 'flex-start' }, mt: 2 }}>
            <Box>
              <Typography variant="subtitle2" color="text.secondary">Date of Birth</Typography>
              <Typography variant="body1" fontWeight="bold">{profile.dateOfBirth}</Typography>
            </Box>
            <Box>
              <Typography variant="subtitle2" color="text.secondary">Gender</Typography>
              <Typography variant="body1" fontWeight="bold">{profile.gender}</Typography>
            </Box>
            <Box>
              <Typography variant="subtitle2" color="text.secondary">Phone</Typography>
              <Typography variant="body1" fontWeight="bold">{profile.phone}</Typography>
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

export default PatientProfileCard;
