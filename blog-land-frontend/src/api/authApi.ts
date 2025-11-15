import apiClient from './apiClient';
import { RegisterRequest } from '../types/auth/requests';
import { LoginRequest } from '../types/auth/requests';

export const registerUser = async (payload: RegisterRequest) => {
  const response = await apiClient.post('/public/auth/register', payload);
  return response;
};

export const loginUser = async (payload: LoginRequest) => {
  const response = await apiClient.post('/public/auth/login', payload);
  return response;
};

export const logoutUser = async () => {
  const response = await apiClient.post('/user/auth/log-out');
  return response;
};
