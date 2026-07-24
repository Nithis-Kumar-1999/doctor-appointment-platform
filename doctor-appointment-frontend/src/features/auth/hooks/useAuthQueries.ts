import { useMutation } from '@tanstack/react-query';
import { authApi } from '../api/auth.api';
import { LoginRequest, RegisterRequest } from '../../../types/auth.types';
import { useAuth } from '../../../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import { useSnackbar } from '../../common/SnackbarContext'; // assuming existence

export const useLogin = () => {
  const { login } = useAuth();
  const navigate = useNavigate();
  const { showSnackbar } = useSnackbar();

  return useMutation({
    mutationFn: (data: LoginRequest) => authApi.login(data),
    onSuccess: (data) => {
      login(data);
      showSnackbar('Login successful', 'success');
      // Redirect based on role
      if (data.role === 'DOCTOR') navigate('/doctor/dashboard');
      else if (data.role === 'PATIENT') navigate('/patient/dashboard');
      else navigate('/');
    },
    onError: (error: any) => {
      const message = error.response?.data?.message || 'Login failed. Please check your credentials.';
      showSnackbar(message, 'error');
    },
  });
};

export const useRegister = () => {
  const { login } = useAuth();
  const navigate = useNavigate();
  const { showSnackbar } = useSnackbar();

  return useMutation({
    mutationFn: (data: RegisterRequest) => authApi.register(data),
    onSuccess: (data) => {
      login(data); // Auto-login after register
      showSnackbar('Registration successful', 'success');
      // Redirect based on role
      if (data.role === 'DOCTOR') navigate('/doctor/dashboard');
      else if (data.role === 'PATIENT') navigate('/patient/dashboard');
      else navigate('/');
    },
    onError: (error: any) => {
      const message = error.response?.data?.message || 'Registration failed.';
      showSnackbar(message, 'error');
    },
  });
};

export const useLogout = () => {
  const { logout } = useAuth();
  const navigate = useNavigate();
  const { showSnackbar } = useSnackbar();

  return useMutation({
    mutationFn: () => authApi.logout(),
    onSuccess: () => {
      logout();
      showSnackbar('Logged out successfully', 'success');
      navigate('/login');
    },
    onError: () => {
      // Even if API fails, clear local storage
      logout();
      navigate('/login');
    },
  });
};
