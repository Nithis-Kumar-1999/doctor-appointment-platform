import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { AuthState, LoginResponse } from '../types/auth.types';
import { tokenStorage } from '../utils/tokenStorage';

interface AuthContextType extends AuthState {
  login: (data: LoginResponse) => void;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [authState, setAuthState] = useState<AuthState>(() => {
    // Initializer function for useState
    const savedUser = localStorage.getItem('userAuth');
    if (savedUser && tokenStorage.getToken()) {
      try {
        const user = JSON.parse(savedUser);
        return { isAuthenticated: true, user };
      } catch {
        // Corrupted storage fallback
        tokenStorage.clearAll();
      }
    }
    return { isAuthenticated: false, user: null };
  });

  // Listen to the custom event emitted by Axios interceptor
  useEffect(() => {
    const handleUnauthorized = () => {
      setAuthState({ isAuthenticated: false, user: null });
    };

    window.addEventListener('auth:unauthorized', handleUnauthorized);
    return () => window.removeEventListener('auth:unauthorized', handleUnauthorized);
  }, []);

  const login = (data: LoginResponse) => {
    tokenStorage.setToken(data.accessToken);
    tokenStorage.setRefreshToken(data.refreshToken);
    
    const user = {
      userId: data.userId,
      email: data.email,
      firstName: data.firstName,
      role: data.role,
    };
    
    localStorage.setItem('userAuth', JSON.stringify(user));
    setAuthState({ isAuthenticated: true, user });
  };

  const logout = () => {
    tokenStorage.clearAll();
    setAuthState({ isAuthenticated: false, user: null });
  };

  return (
    <AuthContext.Provider value={{ ...authState, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

// eslint-disable-next-line react-refresh/only-export-components
export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
