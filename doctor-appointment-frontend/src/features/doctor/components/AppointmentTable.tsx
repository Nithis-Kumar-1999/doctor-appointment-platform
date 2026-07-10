import React, { useState } from 'react';
import { Box, Button, MenuItem, Select, FormControl, InputLabel } from '@mui/material';
import { DataTable } from '../../../components/DataTable';
import StatusChip from '../../../components/StatusChip';
import ConfirmDialog from '../../../components/ConfirmDialog';
import { Appointment } from '../types/doctorTypes';
import { useDoctorAppointments, useUpdateAppointmentStatus } from '../hooks/useDoctorQueries';
import SearchBar from '../../../components/SearchBar';
import FilterPanel from '../../../components/FilterPanel';

const AppointmentTable: React.FC = () => {
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [statusFilter, setStatusFilter] = useState<string>('');
  const [search, setSearch] = useState('');
  
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [selectedAppt, setSelectedAppt] = useState<number | null>(null);
  const [actionType, setActionType] = useState<string>('');

  const { data, isLoading } = useDoctorAppointments(page, rowsPerPage, statusFilter);
  const updateStatusMutation = useUpdateAppointmentStatus();

  const handleActionClick = (id: number, type: string) => {
    setSelectedAppt(id);
    setActionType(type);
    setConfirmOpen(true);
  };

  const executeAction = () => {
    if (selectedAppt) {
      updateStatusMutation.mutate({ id: selectedAppt, status: actionType });
    }
    setConfirmOpen(false);
  };

  const columns = [
    { id: 'id', label: 'ID' },
    { id: 'patientName', label: 'Patient Name', render: (row: Appointment) => row.patientName || `Patient #${row.patientId}` },
    { id: 'appointmentDate', label: 'Date' },
    { id: 'appointmentTime', label: 'Time' },
    { id: 'status', label: 'Status', render: (row: Appointment) => <StatusChip status={row.status} /> },
    { id: 'actions', label: 'Actions', render: (row: Appointment) => (
      <Box sx={{ display: 'flex', gap: 1 }}>
        {row.status === 'SCHEDULED' && (
          <>
            <Button size="small" variant="outlined" color="success" onClick={() => handleActionClick(row.id, 'COMPLETED')}>Complete</Button>
            <Button size="small" variant="outlined" color="error" onClick={() => handleActionClick(row.id, 'CANCELLED')}>Cancel</Button>
          </>
        )}
      </Box>
    )}
  ];

  return (
    <Box>
      <FilterPanel title="Filter Appointments">
        <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap' }}>
          <SearchBar value={search} onChange={setSearch} placeholder="Search patients..." />
          <FormControl sx={{ minWidth: 200 }} size="small">
            <InputLabel>Status</InputLabel>
            <Select value={statusFilter} label="Status" onChange={(e) => { setStatusFilter(e.target.value); setPage(0); }}>
              <MenuItem value="">All Statuses</MenuItem>
              <MenuItem value="SCHEDULED">Scheduled</MenuItem>
              <MenuItem value="COMPLETED">Completed</MenuItem>
              <MenuItem value="CANCELLED">Cancelled</MenuItem>
            </Select>
          </FormControl>
        </Box>
      </FilterPanel>

      <DataTable
        columns={columns}
        data={data?.content || []}
        totalElements={data?.totalElements || 0}
        page={page}
        rowsPerPage={rowsPerPage}
        onPageChange={setPage}
        onRowsPerPageChange={setRowsPerPage}
        isLoading={isLoading}
      />

      <ConfirmDialog
        open={confirmOpen}
        title={`Confirm ${actionType}`}
        content={`Are you sure you want to mark this appointment as ${actionType}?`}
        onConfirm={executeAction}
        onCancel={() => setConfirmOpen(false)}
        isDestructive={actionType === 'CANCELLED'}
      />
    </Box>
  );
};

export default AppointmentTable;
