import { http, HttpResponse } from 'msw';
import { API_BASE_URL } from '../utils/env';

export const handlers = [
  // Authentication Mocks
  http.post(`${API_BASE_URL}/api/v1/auth/login`, async ({ request }) => {
    return HttpResponse.json({
      accessToken: 'mock-jwt-token',
      refreshToken: 'mock-refresh-token',
      userId: 1,
      role: 'PATIENT'
    });
  }),

  // Patient Profile Mocks
  http.get(`${API_BASE_URL}/api/v1/patients/profile/me`, () => {
    return HttpResponse.json({
      id: 1,
      userId: 1,
      dateOfBirth: '1990-01-01',
      gender: 'MALE',
      phone: '1234567890'
    });
  }),
  
  // Dashboard Stats Mock
  http.get(`${API_BASE_URL}/api/v1/patients/me/stats`, () => {
    return HttpResponse.json({
      upcomingAppointments: 2,
      completedAppointments: 5,
      cancelledAppointments: 0
    });
  })
];
