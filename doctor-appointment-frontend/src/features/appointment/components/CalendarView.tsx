import React from 'react';
import { Box, TextField } from '@mui/material';

interface CalendarViewProps {
  value: string;
  onChange: (date: string) => void;
  error?: boolean;
  helperText?: string;
}

const CalendarView: React.FC<CalendarViewProps> = ({ value, onChange, error, helperText }) => {
  // Simple native date picker integration, disabling past dates via min attribute
  const today = new Date().toISOString().split('T')[0];

  return (
    <Box sx={{ maxWidth: 400, mx: 'auto', textAlign: 'center' }}>
      <TextField
        fullWidth
        type="date"
        label="Select Appointment Date"
        InputLabelProps={{ shrink: true }}
        inputProps={{ min: today }}
        value={value}
        onChange={(e) => onChange(e.target.value)}
        error={error}
        helperText={helperText || 'Select a date to see available time slots'}
      />
    </Box>
  );
};

export default CalendarView;
