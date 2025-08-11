import apiClient from './apiClient';
import { RegisterRequest } from '../types/auth/requests';
import { LoginRequest } from '../types/auth/requests';

export const registerUser = async (payload: RegisterRequest) => {
  const response = await apiClient.post('/auth/register', payload);

  return response;
};

export const loginUser = async (payload: LoginRequest) => {
  const response = await apiClient.post('/auth/login', payload);
  return response;
};
