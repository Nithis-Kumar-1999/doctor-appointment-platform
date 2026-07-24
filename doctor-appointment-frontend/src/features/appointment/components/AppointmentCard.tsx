import React from 'react';
import { Card, CardContent, Typography, Box, Button, Divider, Skeleton } from '@mui/material';
import Grid from '@mui/material/Grid';
import PersonIcon from '@mui/icons-material/Person';
import CalendarMonthIcon from '@mui/icons-material/CalendarMonth';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import MedicalServicesIcon from '@mui/icons-material/MedicalServices';
import { AppointmentResponse, AppointmentStatus } from '../types/appointment.types';
import { AppointmentStatusChip } from './AppointmentStatusChip';

interface Props {
  appointment: AppointmentResponse;
  userRole: 'DOCTOR' | 'PATIENT';
  onActionClick?: (appointment: AppointmentResponse, action: string) => void;
}

export const AppointmentCard: React.FC<Props> = ({ appointment, userRole, onActionClick }) => {
  const isPatient = userRole === 'PATIENT';
  const displayName = isPatient 
    ? `Dr. ${appointment.doctorFirstName} ${appointment.doctorLastName}` 
    : `${appointment.patientFirstName} ${appointment.patientLastName}`;

  return (
    <Card elevation={2} sx={{ mb: 2, borderRadius: 2 }}>
      <CardContent>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 2 }}>
          <Box>
            <Typography variant="h6" sx={{ fontWeight: 'bold', display: 'flex', alignItems: 'center', gap: 1 }}>
              <PersonIcon color="action" /> {displayName}
            </Typography>
            {isPatient && (
              <Typography variant="body2" color="text.secondary" sx={{ display: 'flex', alignItems: 'center', gap: 1, mt: 0.5 }}>
                <MedicalServicesIcon fontSize="small" /> {appointment.doctorSpecialty.replace('_', ' ')}
              </Typography>
            )}
          </Box>
          <AppointmentStatusChip status={appointment.status} />
        </Box>

        <Divider sx={{ my: 1.5 }} />

        <Grid container spacing={2}>
          <Grid size={{ xs: 6 }}>
            <Typography variant="body2" color="text.secondary" sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
              <CalendarMonthIcon fontSize="small" /> Date
            </Typography>
            <Typography variant="body1" sx={{ fontWeight: 'medium' }}>
              {appointment.appointmentDate}
            </Typography>
          </Grid>
          <Grid size={{ xs: 6 }}>
            <Typography variant="body2" color="text.secondary" sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
              <AccessTimeIcon fontSize="small" /> Time
            </Typography>
            <Typography variant="body1" sx={{ fontWeight: 'medium' }}>
              {appointment.appointmentTime}
            </Typography>
          </Grid>
        </Grid>

        {appointment.reason && (
          <Box sx={{ mt: 2 }}>
            <Typography variant="body2" color="text.secondary">Reason for visit:</Typography>
            <Typography variant="body2" sx={{ fontStyle: 'italic' }}>"{appointment.reason}"</Typography>
          </Box>
        )}

        {onActionClick && (appointment.status === AppointmentStatus.PENDING || appointment.status === AppointmentStatus.CONFIRMED) && (
          <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 1, mt: 3 }}>
            {userRole === 'DOCTOR' && appointment.status === AppointmentStatus.PENDING && (
              <Button size="small" variant="contained" color="primary" onClick={() => onActionClick(appointment, 'CONFIRM')}>
                Confirm
              </Button>
            )}
            {userRole === 'DOCTOR' && appointment.status === AppointmentStatus.CONFIRMED && (
              <Button size="small" variant="contained" color="success" onClick={() => onActionClick(appointment, 'COMPLETE')}>
                Complete
              </Button>
            )}
            <Button size="small" variant="outlined" color="error" onClick={() => onActionClick(appointment, 'CANCEL')}>
              Cancel
            </Button>
          </Box>
        )}
      </CardContent>
    </Card>
  );
};

export const AppointmentCardSkeleton = () => (
  <Card elevation={2} sx={{ mb: 2, borderRadius: 2 }}>
    <CardContent>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
        <Box>
          <Skeleton variant="text" width={150} height={30} />
          <Skeleton variant="text" width={100} height={20} />
        </Box>
        <Skeleton variant="rectangular" width={80} height={24} sx={{ borderRadius: 1 }} />
      </Box>
      <Divider sx={{ my: 1.5 }} />
      <Grid container spacing={2}>
        <Grid size={{ xs: 6 }}>
          <Skeleton variant="text" width={100} />
        </Grid>
        <Grid size={{ xs: 6 }}>
          <Skeleton variant="text" width={100} />
        </Grid>
      </Grid>
    </CardContent>
  </Card>
);
