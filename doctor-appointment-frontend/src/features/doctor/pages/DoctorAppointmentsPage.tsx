import React, { useState, useMemo } from 'react';
import { Box, Typography, Paper, TextField, MenuItem } from '@mui/material';
import Grid from '@mui/material/Grid';
import { useDoctorAppointments, useUpdateAppointmentStatus } from '../../appointment/hooks/useAppointments';
import { AppointmentStatus, AppointmentResponse } from '../../appointment/types/appointment.types';
import { AppointmentTable } from '../../appointment/components/AppointmentTable';
import { ConfirmationDialog } from '../../../components/ConfirmationDialog';

const DoctorAppointmentsPage: React.FC = () => {
  const [statusFilter, setStatusFilter] = useState<AppointmentStatus | ''>('');
  const [searchName, setSearchName] = useState('');
  const [sortByDate, setSortByDate] = useState<'asc' | 'desc'>('asc');
  
  // Fetch a larger page size to allow for frontend sorting/filtering
  const { data, isLoading } = useDoctorAppointments(statusFilter, 0, 100); 
  const updateStatusMutation = useUpdateAppointmentStatus();
  
  const [actionDialog, setActionDialog] = useState<{ open: boolean, appointment: AppointmentResponse | null, action: 'CONFIRM' | 'COMPLETE' | 'CANCEL' | null }>({
    open: false,
    appointment: null,
    action: null
  });

  const handleActionClick = (appointment: AppointmentResponse, action: 'CONFIRM' | 'COMPLETE' | 'CANCEL') => {
    setActionDialog({ open: true, appointment, action });
  };

  const handleActionConfirm = () => {
    if (actionDialog.appointment && actionDialog.action) {
      let status: AppointmentStatus;
      switch (actionDialog.action) {
        case 'CONFIRM': status = AppointmentStatus.CONFIRMED; break;
        case 'COMPLETE': status = AppointmentStatus.COMPLETED; break;
        case 'CANCEL': status = AppointmentStatus.CANCELLED; break;
      }

      updateStatusMutation.mutate(
        { id: actionDialog.appointment.id, status, reason: actionDialog.action === 'CANCEL' ? 'Cancelled by doctor' : undefined },
        {
          onSuccess: () => setActionDialog({ open: false, appointment: null, action: null })
        }
      );
    }
  };

  // Frontend filtering and sorting
  const processedAppointments = useMemo(() => {
    let list = [...(data?.content || [])];

    if (searchName) {
      const lowerSearch = searchName.toLowerCase();
      list = list.filter(a => 
        a.patientFirstName.toLowerCase().includes(lowerSearch) || 
        a.patientLastName.toLowerCase().includes(lowerSearch)
      );
    }

    list.sort((a, b) => {
      const dateA = new Date(`${a.appointmentDate}T${a.appointmentTime}`).getTime();
      const dateB = new Date(`${b.appointmentDate}T${b.appointmentTime}`).getTime();
      return sortByDate === 'asc' ? dateA - dateB : dateB - dateA;
    });

    return list;
  }, [data?.content, searchName, sortByDate]);

  return (
    <Box sx={{ p: 3, maxWidth: 1200, mx: 'auto' }}>
      <Typography variant="h4" sx={{ fontWeight: 'bold', mb: 3 }}>
        Manage Appointments
      </Typography>

      <Paper sx={{ p: 2, mb: 4 }}>
        <Grid container spacing={2}>
          <Grid size={{ xs: 12, sm: 4 }}>
            <TextField
              fullWidth
              label="Search Patient Name"
              value={searchName}
              onChange={(e) => setSearchName(e.target.value)}
              size="small"
            />
          </Grid>
          <Grid size={{ xs: 12, sm: 4 }}>
            <TextField
              select
              fullWidth
              label="Filter by Status"
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value as AppointmentStatus | '')}
              size="small"
            >
              <MenuItem value="">All Statuses</MenuItem>
              <MenuItem value={AppointmentStatus.PENDING}>Pending</MenuItem>
              <MenuItem value={AppointmentStatus.CONFIRMED}>Confirmed</MenuItem>
              <MenuItem value={AppointmentStatus.COMPLETED}>Completed</MenuItem>
              <MenuItem value={AppointmentStatus.CANCELLED}>Cancelled</MenuItem>
            </TextField>
          </Grid>
          <Grid size={{ xs: 12, sm: 4 }}>
            <TextField
              select
              fullWidth
              label="Sort by Date"
              value={sortByDate}
              onChange={(e) => setSortByDate(e.target.value as 'asc' | 'desc')}
              size="small"
            >
              <MenuItem value="asc">Oldest First</MenuItem>
              <MenuItem value="desc">Newest First</MenuItem>
            </TextField>
          </Grid>
        </Grid>
      </Paper>

      <AppointmentTable 
        appointments={processedAppointments}
        userRole="DOCTOR"
        isLoading={isLoading}
        onActionClick={handleActionClick}
      />

      <ConfirmationDialog
        open={actionDialog.open}
        title={`${actionDialog.action === 'CONFIRM' ? 'Confirm' : actionDialog.action === 'COMPLETE' ? 'Complete' : 'Cancel'} Appointment`}
        message={`Are you sure you want to ${actionDialog.action?.toLowerCase()} the appointment with ${actionDialog.appointment?.patientFirstName} ${actionDialog.appointment?.patientLastName}?`}
        confirmText="Yes, Proceed"
        onConfirm={handleActionConfirm}
        onCancel={() => setActionDialog({ open: false, appointment: null, action: null })}
        isLoading={updateStatusMutation.isPending}
      />
    </Box>
  );
};

export default DoctorAppointmentsPage;
