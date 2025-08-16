// apiClient.ts
import axios from 'axios';
import { store } from '../store/store';

// http://localhost:8080/api
export const apiClient = axios.create({
  baseURL: 'https://blog-land.onrender.com',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Attach token from Redux state to every request
apiClient.interceptors.request.use(
  (config) => {
    const token = store.getState().auth.jwtToken;
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

export default apiClient;
