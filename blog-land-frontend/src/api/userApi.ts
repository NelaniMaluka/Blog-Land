import apiClient from './apiClient';
import { UpdateUserRequest } from '../types/user/request';

export const updateProfileIcon = async (file: File) => {
  const formData = new FormData();
  formData.append('file', file);

  const response = await apiClient.post('/user/upload-profile-image', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
  return response.data;
};

export const removeProfileIcon = async () => {
  const response = await apiClient.delete('/user/remove-profile-image', {});
  return response.data;
};

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

export const logoutUser = async () => {
  const response = await apiClient.post('/user/log-out', {});
  return response;
};
