import React from 'react';
import { Backdrop, CircularProgress, Typography, Box } from '@mui/material';

interface LoadingOverlayProps {
  open: boolean;
  message?: string;
}

/**
 * Reusable full-screen loading overlay for blocking UI interactions during critical operations (e.g., submitting a payment).
 */
const LoadingOverlay: React.FC<LoadingOverlayProps> = ({ open, message = 'Please wait...' }) => {
  return (
    <Backdrop
      sx={{ 
        color: '#fff', 
        zIndex: (theme) => theme.zIndex.drawer + 999,
        display: 'flex',
        flexDirection: 'column',
        gap: 2
      }}
      open={open}
    >
      <CircularProgress color="inherit" />
      <Typography variant="h6">{message}</Typography>
    </Backdrop>
  );
};

export default LoadingOverlay;
