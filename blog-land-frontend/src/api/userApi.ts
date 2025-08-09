import apiClient from './apiClient';
import { UpdateUserRequest } from '../types/user/request';

export const getUserDetails = async () => {
  const response = await apiClient.get('/user/get-user', {});
  return response;
};

export const updateUserDetails = async (payload: UpdateUserRequest) => {
  const response = await apiClient.put('/user/update-user', payload);
  return response;
};

export const deleteUserDetails = async () => {
  const response = await apiClient.delete('/user/delete-user', {});
  return response;
};
