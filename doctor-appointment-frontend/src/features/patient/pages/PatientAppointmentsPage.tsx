import React, { useState } from 'react';
import { Box, Typography, Paper, TextField, MenuItem } from '@mui/material';
import Grid from '@mui/material/Grid';
import { usePatientAppointments, useUpdateAppointmentStatus } from '../../appointment/hooks/useAppointments';
import { AppointmentStatus, AppointmentResponse } from '../../appointment/types/appointment.types';
import { AppointmentTable } from '../../appointment/components/AppointmentTable';
import { ConfirmationDialog } from '../../../components/ConfirmationDialog';

const PatientAppointmentsPage: React.FC = () => {
  const [statusFilter, setStatusFilter] = useState<AppointmentStatus | ''>('');
  
  const { data, isLoading } = usePatientAppointments(statusFilter, 0, 50); // Get up to 50 for simplicity
  const updateStatusMutation = useUpdateAppointmentStatus();
  
  const [cancelDialog, setCancelDialog] = useState<{ open: boolean, appointment: AppointmentResponse | null }>({
    open: false,
    appointment: null
  });

  const handleActionClick = (appointment: AppointmentResponse, action: 'CONFIRM' | 'COMPLETE' | 'CANCEL') => {
    if (action === 'CANCEL') {
      setCancelDialog({ open: true, appointment });
    }
  };

  const handleCancelConfirm = () => {
    if (cancelDialog.appointment) {
      updateStatusMutation.mutate(
        { id: cancelDialog.appointment.id, status: AppointmentStatus.CANCELLED, reason: 'Patient requested cancellation' },
        {
          onSuccess: () => setCancelDialog({ open: false, appointment: null })
        }
      );
    }
  };

  return (
    <Box sx={{ p: 3, maxWidth: 1200, mx: 'auto' }}>
      <Typography variant="h4" sx={{ fontWeight: 'bold', mb: 3 }}>
        My Appointments
      </Typography>

      <Paper sx={{ p: 2, mb: 4 }}>
        <Grid container spacing={2}>
          <Grid size={{ xs: 12, sm: 4 }}>
            <TextField
              select
              fullWidth
              label="Filter by Status"
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value as AppointmentStatus | '')}
              size="small"
            >
              <MenuItem value="">All Appointments</MenuItem>
              <MenuItem value={AppointmentStatus.PENDING}>Pending</MenuItem>
              <MenuItem value={AppointmentStatus.CONFIRMED}>Confirmed</MenuItem>
              <MenuItem value={AppointmentStatus.COMPLETED}>Completed</MenuItem>
              <MenuItem value={AppointmentStatus.CANCELLED}>Cancelled</MenuItem>
            </TextField>
          </Grid>
        </Grid>
      </Paper>

      <AppointmentTable 
        appointments={data?.content || []}
        userRole="PATIENT"
        isLoading={isLoading}
        onActionClick={handleActionClick}
      />

      <ConfirmationDialog
        open={cancelDialog.open}
        title="Cancel Appointment"
        message={`Are you sure you want to cancel your appointment with Dr. ${cancelDialog.appointment?.doctorLastName} on ${cancelDialog.appointment?.appointmentDate}?`}
        confirmText="Yes, Cancel"
        onConfirm={handleCancelConfirm}
        onCancel={() => setCancelDialog({ open: false, appointment: null })}
        isLoading={updateStatusMutation.isPending}
      />
    </Box>
  );
};

export default PatientAppointmentsPage;
