import React from 'react';
import { Box, Chip, Typography } from '@mui/material';

interface Props {
  slots: string[];
  selectedSlot: string | null;
  onSelectSlot: (slot: string) => void;
  isLoading?: boolean;
}

export const SlotPicker: React.FC<Props> = ({ slots, selectedSlot, onSelectSlot, isLoading }) => {
  if (isLoading) {
    return (
      <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap' }}>
        {[1, 2, 3, 4].map((i) => (
          <Chip key={i} label="00:00:00" disabled sx={{ visibility: 'hidden' }} />
        ))}
      </Box>
    );
  }

  if (slots.length === 0) {
    return (
      <Typography variant="body2" color="text.secondary">
        No slots available for this day.
      </Typography>
    );
  }

  return (
    <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap' }}>
      {slots.map((slot) => {
        // Format the time to be more user friendly, e.g. "10:30"
        const formattedSlot = slot.substring(0, 5);
        return (
          <Chip
            key={slot}
            label={formattedSlot}
            clickable
            color={selectedSlot === slot ? 'primary' : 'default'}
            onClick={() => onSelectSlot(slot)}
            variant={selectedSlot === slot ? 'filled' : 'outlined'}
            sx={{ fontWeight: selectedSlot === slot ? 'bold' : 'normal' }}
          />
        );
      })}
    </Box>
  );
};
