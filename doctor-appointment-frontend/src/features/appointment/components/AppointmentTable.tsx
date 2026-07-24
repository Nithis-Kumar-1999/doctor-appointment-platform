import React from 'react';
import { 
  Table, TableBody, TableCell, TableContainer, TableHead, TableRow, 
  Paper, IconButton, Tooltip, Skeleton, Box, Typography 
} from '@mui/material';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import CancelIcon from '@mui/icons-material/Cancel';
import DoneAllIcon from '@mui/icons-material/DoneAll';
import { AppointmentResponse, AppointmentStatus } from '../types/appointment.types';
import { AppointmentStatusChip } from './AppointmentStatusChip';

interface Props {
  appointments: AppointmentResponse[];
  userRole: 'DOCTOR' | 'PATIENT';
  isLoading?: boolean;
  onActionClick?: (appointment: AppointmentResponse, action: 'CONFIRM' | 'COMPLETE' | 'CANCEL') => void;
}

export const AppointmentTable: React.FC<Props> = ({ appointments, userRole, isLoading, onActionClick }) => {

  if (isLoading) {
    return (
      <TableContainer component={Paper} elevation={2}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell><Skeleton /></TableCell>
              <TableCell><Skeleton /></TableCell>
              <TableCell><Skeleton /></TableCell>
              <TableCell><Skeleton /></TableCell>
              <TableCell><Skeleton /></TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {[1, 2, 3].map(i => (
              <TableRow key={i}>
                <TableCell><Skeleton /></TableCell>
                <TableCell><Skeleton /></TableCell>
                <TableCell><Skeleton /></TableCell>
                <TableCell><Skeleton /></TableCell>
                <TableCell><Skeleton /></TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    );
  }

  if (!appointments.length) {
    return (
      <Box sx={{ p: 4, textAlign: 'center', bgcolor: 'background.paper', borderRadius: 2, border: '1px dashed grey' }}>
        <Typography color="text.secondary">No appointments found.</Typography>
      </Box>
    );
  }

  return (
    <TableContainer component={Paper} elevation={2} sx={{ borderRadius: 2 }}>
      <Table sx={{ minWidth: 650 }} aria-label="appointments table">
        <TableHead sx={{ bgcolor: 'grey.50' }}>
          <TableRow>
            <TableCell sx={{ fontWeight: 'bold' }}>{userRole === 'PATIENT' ? 'Doctor' : 'Patient'}</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Date</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Time</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Status</TableCell>
            <TableCell align="right" sx={{ fontWeight: 'bold' }}>Actions</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {appointments.map((apt) => (
            <TableRow key={apt.id} sx={{ '&:last-child td, &:last-child th': { border: 0 } }}>
              <TableCell>
                {userRole === 'PATIENT' 
                  ? `Dr. ${apt.doctorFirstName} ${apt.doctorLastName}`
                  : `${apt.patientFirstName} ${apt.patientLastName}`
                }
              </TableCell>
              <TableCell>{apt.appointmentDate}</TableCell>
              <TableCell>{apt.appointmentTime}</TableCell>
              <TableCell>
                <AppointmentStatusChip status={apt.status} />
              </TableCell>
              <TableCell align="right">
                {onActionClick && (
                  <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 1 }}>
                    {userRole === 'DOCTOR' && apt.status === AppointmentStatus.PENDING && (
                      <Tooltip title="Confirm Appointment">
                        <IconButton size="small" color="primary" onClick={() => onActionClick(apt, 'CONFIRM')}>
                          <CheckCircleIcon />
                        </IconButton>
                      </Tooltip>
                    )}
                    {userRole === 'DOCTOR' && apt.status === AppointmentStatus.CONFIRMED && (
                      <Tooltip title="Mark as Completed">
                        <IconButton size="small" color="success" onClick={() => onActionClick(apt, 'COMPLETE')}>
                          <DoneAllIcon />
                        </IconButton>
                      </Tooltip>
                    )}
                    {(apt.status === AppointmentStatus.PENDING || apt.status === AppointmentStatus.CONFIRMED) && (
                      <Tooltip title="Cancel Appointment">
                        <IconButton size="small" color="error" onClick={() => onActionClick(apt, 'CANCEL')}>
                          <CancelIcon />
                        </IconButton>
                      </Tooltip>
                    )}
                  </Box>
                )}
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
};
