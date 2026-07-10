import React from 'react';
import ConfirmDialog from '../../../components/ConfirmDialog';

interface AppointmentConfirmationDialogProps {
  open: boolean;
  onConfirm: () => void;
  onCancel: () => void;
  isLoading: boolean;
}

const AppointmentConfirmationDialog: React.FC<AppointmentConfirmationDialogProps> = ({ open, onConfirm, onCancel, isLoading }) => {
  return (
    <ConfirmDialog
      open={open}
      title="Confirm Booking"
      content="Are you sure you want to book this appointment? Once confirmed, you can view it in your appointments history."
      onConfirm={onConfirm}
      onCancel={onCancel}
      confirmText={isLoading ? 'Booking...' : 'Confirm'}
    />
  );
};

export default AppointmentConfirmationDialog;
