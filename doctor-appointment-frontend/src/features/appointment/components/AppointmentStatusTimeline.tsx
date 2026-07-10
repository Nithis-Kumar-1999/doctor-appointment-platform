import React from 'react';
import { Box, Stepper, Step, StepLabel, Typography } from '@mui/material';

interface AppointmentStatusTimelineProps {
  status: 'SCHEDULED' | 'CONFIRMED' | 'COMPLETED' | 'CANCELLED' | 'PENDING';
}

const STEPS = ['Scheduled', 'Confirmed', 'Completed'];

const AppointmentStatusTimeline: React.FC<AppointmentStatusTimelineProps> = ({ status }) => {
  let activeStep = 0;
  let isCancelled = false;

  switch (status) {
    case 'PENDING':
    case 'SCHEDULED':
      activeStep = 0;
      break;
    case 'CONFIRMED':
      activeStep = 1;
      break;
    case 'COMPLETED':
      activeStep = 3; // All done
      break;
    case 'CANCELLED':
      isCancelled = true;
      activeStep = 1; // Mark failed at some point
      break;
  }

  return (
    <Box sx={{ width: '100%', my: 4 }}>
      {isCancelled ? (
        <Typography color="error" variant="h6" align="center" fontWeight="bold">
          Appointment Cancelled
        </Typography>
      ) : (
        <Stepper activeStep={activeStep} alternativeLabel>
          {STEPS.map((label) => (
            <Step key={label}>
              <StepLabel>{label}</StepLabel>
            </Step>
          ))}
        </Stepper>
      )}
    </Box>
  );
};

export default AppointmentStatusTimeline;
