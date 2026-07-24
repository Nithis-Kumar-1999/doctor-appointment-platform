import React from 'react';
import { Chip, ChipProps } from '@mui/material';
import { AppointmentStatus } from '../types/appointment.types';

interface Props extends Omit<ChipProps, 'color'> {
  status: AppointmentStatus | string;
}

const getStatusColor = (status: string): ChipProps['color'] => {
  switch (status) {
    case AppointmentStatus.PENDING:
      return 'warning';
    case AppointmentStatus.CONFIRMED:
      return 'primary';
    case AppointmentStatus.COMPLETED:
      return 'success';
    case AppointmentStatus.CANCELLED:
      return 'error';
    default:
      return 'default';
  }
};

export const AppointmentStatusChip: React.FC<Props> = ({ status, ...props }) => {
  return (
    <Chip 
      label={status} 
      color={getStatusColor(status as string)} 
      size="small" 
      sx={{ fontWeight: 'bold' }}
      {...props} 
    />
  );
};
