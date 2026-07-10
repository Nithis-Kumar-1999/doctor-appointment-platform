import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect } from 'vitest';
import { render } from '../../../../test-utils/test-utils';
import LoginPage from '../LoginPage';

describe('LoginPage Integration', () => {
  it('renders login form correctly', () => {
    render(<LoginPage />);
    expect(screen.getByRole('heading', { name: /welcome back/i })).toBeInTheDocument();
    expect(screen.getByLabelText(/email address/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/password/i)).toBeInTheDocument();
  });

  it('shows validation errors for empty submission', async () => {
    render(<LoginPage />);
    const submitButton = screen.getByRole('button', { name: /sign in/i });
    
    await userEvent.click(submitButton);
    
    expect(await screen.findByText(/invalid email format/i)).toBeInTheDocument();
    expect(await screen.findByText(/password is required/i)).toBeInTheDocument();
  });

  it('submits successfully and redirects using MSW mock', async () => {
    render(<LoginPage />);
    
    const emailInput = screen.getByLabelText(/email address/i);
    const passInput = screen.getByLabelText(/password/i);
    const submitButton = screen.getByRole('button', { name: /sign in/i });

    await userEvent.type(emailInput, 'patient@test.com');
    await userEvent.type(passInput, 'Password123!');
    await userEvent.click(submitButton);

    // MSW will intercept the request and return success.
    // The AuthContext will catch it and we can assert on UI changes (e.g. snackbar or mock router)
    await waitFor(() => {
      expect(screen.getByText(/login successful/i)).toBeInTheDocument();
    });
  });
});
