import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import { ThemeProvider, CssBaseline } from '@mui/material';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import AppRoutes from './routes/AppRoutes';
import { AuthProvider } from './context/AuthContext';
import { ThemeContextProvider } from './context/ThemeContext';
import { SnackbarProvider } from './features/common/SnackbarContext';
import ErrorBoundary from './components/ErrorBoundary';
import { useNetworkStatus } from './hooks/useNetworkStatus';
import { Alert, Snackbar } from '@mui/material';

// Initialize React Query Client with production-optimized defaults
const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 1, // Don't aggressively retry on 404s
      refetchOnWindowFocus: false, // Prevent unnecessary refetches when switching tabs
      staleTime: 5 * 60 * 1000, // Data is considered fresh for 5 minutes
      gcTime: 10 * 60 * 1000, // Keep inactive data in cache for 10 minutes
    },
  },
});

 * Root Application Component.
 * Wraps the application in all necessary Context Providers (Theme, Query, Auth, Router).
 */
function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <ThemeContextProvider>
        <SnackbarProvider>
          <AuthProvider>
            <BrowserRouter>
              <AppRoutes />
            </BrowserRouter>
          </AuthProvider>
        </SnackbarProvider>
      </ThemeContextProvider>
    </QueryClientProvider>
    </>
  );
}

export default function App() {
  return (
    <ErrorBoundary>
      <AppWrapper />
    </ErrorBoundary>
  );
}
