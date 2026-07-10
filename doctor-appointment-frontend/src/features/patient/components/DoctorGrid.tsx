import React from 'react';
import { Grid, Typography, Box } from '@mui/material';
import DoctorSearchCard from './DoctorSearchCard';
import { DoctorProfile } from '../../doctor/types/doctorTypes';

interface DoctorGridProps {
  doctors: DoctorProfile[];
}

const DoctorGrid: React.FC<DoctorGridProps> = ({ doctors }) => {
  if (doctors.length === 0) {
    return (
      <Box sx={{ py: 8, textAlign: 'center' }}>
        <Typography variant="h6" color="text.secondary">No doctors found matching your criteria.</Typography>
      </Box>
    );
  }

  return (
    <Grid container spacing={3}>
      {doctors.map(doctor => (
        <Grid item xs={12} sm={6} md={4} lg={3} key={doctor.id}>
          <DoctorSearchCard doctor={doctor} />
        </Grid>
      ))}
    </Grid>
  );
};

export default DoctorGrid;
