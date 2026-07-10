import { useMutation } from '@tanstack/react-query';
import { authService } from '../services/authService';
import { LoginRequest, RegisterRequest } from '../types/authTypes';
import { useAuth } from '../../../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import { setTokens, clearTokens } from '../utils/tokenUtils';

export const useLoginMutation = () => {
  const { login } = useAuth();
  const navigate = useNavigate();

  return useMutation({
    mutationFn: (data: LoginRequest & { rememberMe?: boolean }) => authService.login(data),
    onSuccess: (data, variables) => {
      setTokens(data.accessToken, data.refreshToken, variables.rememberMe);
      login(data.accessToken, data.refreshToken);
      navigate('/dashboard', { replace: true });
    },
  });
};

export const useRegisterMutation = () => {
  const { login } = useAuth();
  const navigate = useNavigate();

  return useMutation({
    mutationFn: (data: RegisterRequest) => authService.register(data),
    onSuccess: (data) => {
      setTokens(data.accessToken, data.refreshToken, true);
      login(data.accessToken, data.refreshToken);
      navigate('/dashboard', { replace: true });
    },
  });
};

export const useLogoutMutation = () => {
  const { logout } = useAuth();
  const navigate = useNavigate();

  return useMutation({
    mutationFn: () => authService.logout(),
    onSettled: () => {
      // Regardless of whether the server call succeeds or fails, wipe local state
      clearTokens();
      logout();
      navigate('/login', { replace: true });
    }
  });
};
