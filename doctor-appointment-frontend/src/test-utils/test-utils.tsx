import React, { ReactElement } from 'react';
import { render, RenderOptions } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { MemoryRouter } from 'react-router-dom';
import { ThemeContextProvider } from '../context/ThemeContext';
import { SnackbarProvider } from '../features/common/SnackbarContext';
import { AuthProvider } from '../context/AuthContext';

// Create a new QueryClient for each test to prevent cache leakage
const createTestQueryClient = () => new QueryClient({
  defaultOptions: {
    queries: {
      retry: false, // Turn off retries for predictable testing
    },
  },
});

const AllTheProviders = ({ children }: { children: React.ReactNode }) => {
  const queryClient = createTestQueryClient();
  return (
    <QueryClientProvider client={queryClient}>
      <ThemeContextProvider>
        <SnackbarProvider>
          <AuthProvider>
            <MemoryRouter>
              {children}
            </MemoryRouter>
          </AuthProvider>
        </SnackbarProvider>
      </ThemeContextProvider>
    </QueryClientProvider>
  );
};

const customRender = (
  ui: ReactElement,
  options?: Omit<RenderOptions, 'wrapper'>,
) => render(ui, { wrapper: AllTheProviders, ...options });

export * from '@testing-library/react';
export { customRender as render };
