// apiClient.ts
import axios from 'axios';
// import { store } from '../store/store';

export const apiClient = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// // Attach token from Redux state to every request
// apiClient.interceptors.request.use(
//   (config) => {
//     const token = store.getState().auth.token;
//     if (token) {
//       config.headers.Authorization = `Bearer ${token}`;
//     }
//     return config;
//   },
//   (error) => Promise.reject(error)
// );

export default apiClient;
