import apiClient from './apiClient';
import { changePasswordRequest, changePasswordWithTokenRequest } from '../types/password/request';

export const changePassword = async (payload: changePasswordRequest) => {
  const response = await apiClient.post('/password/change', {
    payload,
  });
  return response;
};

export const forgotPassword = async (payload: { email: string }) => {
  const response = await apiClient.post('/auth/request-password-reset', {
    payload,
  });
  return response;
};

export const changePasswordWithToken = async (payload: changePasswordWithTokenRequest) => {
  const response = await apiClient.post('/auth/change-password', {
    payload,
  });
  return response;
};
