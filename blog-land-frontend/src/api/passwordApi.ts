import apiClient from './apiClient';
import { changePasswordRequest, forgotPasswordRequest } from '../types/password/request';

export const requestPasswordReset = async (email: string) => {
  const response = await apiClient.post('/public/password/reset', email);
  return response;
};

export const resetPassword = async (token: string, payload: forgotPasswordRequest) => {
  const response = await apiClient.put(`/public/password/reset/${token}`, {
    payload,
  });
  return response;
};

export const changePassword = async (payload: changePasswordRequest) => {
  const response = await apiClient.put('/user/password', {
    payload,
  });
  return response;
};
