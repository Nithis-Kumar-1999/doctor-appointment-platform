import React, { ReactNode } from 'react';
import { Box, Container, SxProps, Theme } from '@mui/material';

interface PageContainerProps {
  children: ReactNode;
  maxWidth?: 'xs' | 'sm' | 'md' | 'lg' | 'xl' | false;
  sx?: SxProps<Theme>;
}

/**
 * Reusable container wrapping every dashboard page to ensure consistent padding and max-width.
 */
const PageContainer: React.FC<PageContainerProps> = ({ children, maxWidth = 'lg', sx }) => {
  return (
    <Box component="main" sx={{ flexGrow: 1, p: { xs: 2, sm: 3, md: 4 }, minHeight: '100vh', ...sx }}>
      <Container maxWidth={maxWidth} disableGutters>
        {children}
      </Container>
    </Box>
  );
};

export default PageContainer;
