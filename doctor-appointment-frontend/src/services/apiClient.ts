import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios';
import { tokenStorage } from '../utils/tokenStorage';

const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

export const apiClient = axios.create({
  baseURL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request Interceptor: Attach JWT Token
apiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = tokenStorage.getToken();
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response Interceptor: Global Error Handling & 401 Logout
apiClient.interceptors.response.use(
  (response) => response,
  (error: AxiosError) => {
    // If we get a 401 Unauthorized (and it's not the login endpoint itself), we should log out.
    if (error.response?.status === 401 && !error.config?.url?.includes('/api/auth/login')) {
      // Clear token and trigger a custom event that AuthContext can listen to, or reload
      tokenStorage.clearAll();
      window.dispatchEvent(new Event('auth:unauthorized'));
    }
    
    // Pass the error down to React Query / the component
    return Promise.reject(error);
  }
);
