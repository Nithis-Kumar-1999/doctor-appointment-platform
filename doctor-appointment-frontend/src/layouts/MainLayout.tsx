import React from 'react';
import { Outlet, useNavigate } from 'react-router-dom';
import { Box, AppBar, Toolbar, Typography, Container, Button } from '@mui/material';
import { useAuth } from '../context/AuthContext';

/**
 * Main application layout wrapping all routes.
 * Includes the navigation bar, main content area, and footer.
 */
const MainLayout = () => {
  const { isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', minHeight: '100vh', bgcolor: 'background.default' }}>
      
      {/* Navigation Bar */}
      <AppBar 
        position="sticky" 
        elevation={0} 
        sx={{ 
          borderBottom: '1px solid', 
          borderColor: 'divider', 
          bgcolor: 'background.paper',
          color: 'text.primary'
        }}
      >
        <Toolbar>
          <Typography 
            variant="h6" 
            component="div" 
            sx={{ flexGrow: 1, fontWeight: 'bold', color: 'primary.main', cursor: 'pointer' }}
            onClick={() => navigate('/')}
          >
            CarePortal
          </Typography>
          
          {isAuthenticated ? (
            <Button color="inherit" onClick={handleLogout} sx={{ fontWeight: 'bold' }}>
              Logout
            </Button>
          ) : (
            <Button color="primary" variant="contained" onClick={() => navigate('/login')}>
              Login
            </Button>
          )}
        </Toolbar>
      </AppBar>
      
      {/* Main Content Area */}
      <Container component="main" maxWidth="lg" sx={{ flexGrow: 1, py: { xs: 3, md: 6 } }}>
        <Outlet />
      </Container>
      
      {/* Footer */}
      <Box component="footer" sx={{ py: 3, px: 2, mt: 'auto', backgroundColor: 'background.paper', borderTop: '1px solid #E2E8F0' }}>
        <Container maxWidth="sm">
          <Typography variant="body2" color="text.secondary" align="center">
            {'© '}
            {new Date().getFullYear()}
            {' Healthcare Tech Team. All rights reserved.'}
          </Typography>
        </Container>
      </Box>
    </Box>
  );
};

export default MainLayout;
