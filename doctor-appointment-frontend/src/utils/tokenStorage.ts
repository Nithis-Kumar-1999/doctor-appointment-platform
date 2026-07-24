export const tokenStorage = {
  getToken: () => localStorage.getItem('accessToken'),
  setToken: (token: string) => localStorage.setItem('accessToken', token),
  clearToken: () => localStorage.removeItem('accessToken'),
  
  getRefreshToken: () => localStorage.getItem('refreshToken'),
  setRefreshToken: (token: string) => localStorage.setItem('refreshToken', token),
  clearRefreshToken: () => localStorage.removeItem('refreshToken'),
  
  clearAll: () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('userAuth');
  }
};
