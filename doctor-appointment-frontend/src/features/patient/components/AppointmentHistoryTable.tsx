import React, { useState } from 'react';
import { Box, Button, MenuItem, Select, FormControl, InputLabel } from '@mui/material';
import { DataTable } from '../../../components/DataTable';
import StatusChip from '../../../components/StatusChip';
import FilterPanel from '../../../components/FilterPanel';
import { Appointment } from '../../doctor/types/doctorTypes';
import { usePatientAppointments } from '../hooks/usePatientQueries';
import VisibilityIcon from '@mui/icons-material/Visibility';

const AppointmentHistoryTable: React.FC = () => {
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [statusFilter, setStatusFilter] = useState<string>('');

  const { data, isLoading } = usePatientAppointments(page, rowsPerPage, statusFilter);

  const columns = [
    { id: 'id', label: 'ID' },
    // A patient sees the Doctor's name, ideally backend populates this, using a placeholder for now
    { id: 'doctorName', label: 'Doctor', render: (row: any) => row.doctorName || `Doctor #${row.doctorId}` },
    { id: 'appointmentDate', label: 'Date' },
    { id: 'appointmentTime', label: 'Time' },
    { id: 'status', label: 'Status', render: (row: Appointment) => <StatusChip status={row.status} /> },
    { id: 'actions', label: 'Actions', render: () => (
      <Button size="small" startIcon={<VisibilityIcon />}>Details</Button>
    )}
  ];

  return (
    <Box>
      <FilterPanel title="Filter History">
        <FormControl sx={{ minWidth: 200 }} size="small">
          <InputLabel>Status</InputLabel>
          <Select value={statusFilter} label="Status" onChange={(e) => { setStatusFilter(e.target.value); setPage(0); }}>
            <MenuItem value="">All Statuses</MenuItem>
            <MenuItem value="SCHEDULED">Scheduled</MenuItem>
            <MenuItem value="COMPLETED">Completed</MenuItem>
            <MenuItem value="CANCELLED">Cancelled</MenuItem>
          </Select>
        </FormControl>
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
    </Box>
  );
};

export default AppointmentHistoryTable;
