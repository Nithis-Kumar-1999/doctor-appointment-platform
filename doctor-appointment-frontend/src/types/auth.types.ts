export type Role = 'PATIENT' | 'DOCTOR' | 'ADMIN';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  role: Role;
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  userId: number;
  email: string;
  firstName: string;
  role: Role;
}

export interface AuthState {
  isAuthenticated: boolean;
  user: {
    userId: number;
    email: string;
    firstName: string;
    role: Role;
  } | null;
}
