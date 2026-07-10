import React from 'react';
import { Chip, ChipProps } from '@mui/material';

interface StatusChipProps extends Omit<ChipProps, 'color'> {
  status: string;
}

/**
 * Standardized status badge for tables and cards.
 * Automatically maps healthcare statuses to MUI colors.
 */
const StatusChip: React.FC<StatusChipProps> = ({ status, ...props }) => {
  let color: ChipProps['color'] = 'default';

  switch (status.toUpperCase()) {
    case 'SCHEDULED':
    case 'CONFIRMED':
    case 'ACTIVE':
      color = 'info';
      break;
    case 'COMPLETED':
      color = 'success';
      break;
    case 'CANCELLED':
    case 'REJECTED':
      color = 'error';
      break;
    case 'PENDING':
      color = 'warning';
      break;
    default:
      color = 'default';
  }

  return (
    <Chip 
      label={status} 
      color={color} 
      size="small" 
      sx={{ fontWeight: 'bold', borderRadius: '4px' }} 
      {...props} 
    />
  );
};

export default StatusChip;
