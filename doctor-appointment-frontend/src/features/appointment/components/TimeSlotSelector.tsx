import React from 'react';
import { Box, Typography, Button, CircularProgress } from '@mui/material';

interface TimeSlotSelectorProps {
  slots: string[];
  selectedSlot: string;
  onSelectSlot: (slot: string) => void;
  isLoading: boolean;
  error?: boolean;
  helperText?: string;
}

const TimeSlotSelector: React.FC<TimeSlotSelectorProps> = ({ slots, selectedSlot, onSelectSlot, isLoading, error, helperText }) => {
  if (isLoading) {
    return <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}><CircularProgress /></Box>;
  }

  if (slots.length === 0) {
    return <Typography color="text.secondary" align="center">No available slots for the selected date.</Typography>;
  }

  return (
    <Box>
      <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 2, justifyContent: 'center' }}>
        {slots.map(slot => (
          <Button
            key={slot}
            variant={selectedSlot === slot ? 'contained' : 'outlined'}
            onClick={() => onSelectSlot(slot)}
            sx={{ minWidth: 100 }}
          >
            {slot}
          </Button>
        ))}
      </Box>
      {error && <Typography color="error" variant="caption" display="block" align="center" sx={{ mt: 2 }}>{helperText}</Typography>}
    </Box>
  );
};

export default TimeSlotSelector;
