import React from 'react';
import { Box, CircularProgress } from '@mui/material';

/**
 * Reusable full-screen loading spinner component.
 * Used for React Suspense fallbacks and data fetching states.
 */
const Loading = () => {
  return (
    <Box 
      sx={{ 
        display: 'flex', 
        justifyContent: 'center', 
        alignItems: 'center', 
        height: '100vh',
        bgcolor: 'background.default'
      }}
    >
      <CircularProgress size={60} thickness={4} color="primary" />
    </Box>
  );
};

export default Loading;
