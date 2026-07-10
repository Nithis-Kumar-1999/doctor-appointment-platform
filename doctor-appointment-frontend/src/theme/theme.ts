import { createTheme, PaletteMode } from '@mui/material/styles';

export const getTheme = (mode: PaletteMode) => createTheme({
  palette: {
    mode,
    primary: {
      main: mode === 'light' ? '#2C7A7B' : '#4FD1C5',
      light: mode === 'light' ? '#4FD1C5' : '#81E6D9',
      dark: mode === 'light' ? '#285E61' : '#319795',
      contrastText: mode === 'light' ? '#ffffff' : '#1A202C',
    },
    secondary: {
      main: mode === 'light' ? '#3182CE' : '#63B3ED',
    },
    background: {
      default: mode === 'light' ? '#F7FAFC' : '#1A202C',
      paper: mode === 'light' ? '#FFFFFF' : '#2D3748',
    },
    success: { main: '#48BB78' },
    warning: { main: '#ED8936' },
    error: { main: '#F56565' },
    info: { main: '#4299E1' },
    text: {
      primary: mode === 'light' ? '#2D3748' : '#F7FAFC',
      secondary: mode === 'light' ? '#718096' : '#A0AEC0',
    }
  },
  typography: {
    fontFamily: '"Inter", "Roboto", "Helvetica", "Arial", sans-serif',
    h1: { fontWeight: 700, fontSize: '2.5rem' },
    h2: { fontWeight: 700, fontSize: '2rem' },
    h3: { fontWeight: 600, fontSize: '1.75rem' },
    h4: { fontWeight: 600, fontSize: '1.5rem' },
    h5: { fontWeight: 600, fontSize: '1.25rem' },
    h6: { fontWeight: 600, fontSize: '1rem' },
    button: { textTransform: 'none', fontWeight: 600 },
  },
  shape: {
    borderRadius: 8,
  },
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          boxShadow: 'none',
          '&:hover': { boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1)' },
        },
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          boxShadow: mode === 'light' 
            ? '0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px 0 rgba(0, 0, 0, 0.06)' 
            : '0 4px 6px -1px rgba(0, 0, 0, 0.3)',
          border: `1px solid ${mode === 'light' ? '#E2E8F0' : '#4A5568'}`,
          backgroundImage: 'none',
        }
      }
    },
    MuiAppBar: {
      styleOverrides: {
        root: {
          backgroundColor: mode === 'light' ? '#FFFFFF' : '#2D3748',
          color: mode === 'light' ? '#2D3748' : '#F7FAFC',
          borderBottom: `1px solid ${mode === 'light' ? '#E2E8F0' : '#4A5568'}`,
          boxShadow: 'none',
          backgroundImage: 'none',
        }
      }
    },
    MuiDrawer: {
      styleOverrides: {
        paper: {
          backgroundColor: mode === 'light' ? '#FFFFFF' : '#2D3748',
          borderRight: `1px solid ${mode === 'light' ? '#E2E8F0' : '#4A5568'}`,
        }
      }
    }
  },
});
