import apiClient from './apiClient';
import { UpdateUserRequest } from '../types/user/request';

export const updateProfileIcon = async (file: File) => {
  const formData = new FormData();
  formData.append('file', file);

  const response = await apiClient.post('/user/image/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
  return response.data;
};

export const removeProfileIcon = async () => {
  const response = await apiClient.delete('/user/image/remove');
  return response.data;
};

export const getPublicUserDetails = async (nanoId: string) => {
  const response = await apiClient.get(`/public/user/${nanoId}`);
  return response;
};

export const getUserDetails = async () => {
  const response = await apiClient.get('/user/me');
  return response;
};

export const updateUserDetails = async (payload: UpdateUserRequest) => {
  const response = await apiClient.put('/user/update', payload);

  return response;
};

export const deleteUserDetails = async () => {
  const response = await apiClient.delete('/user/remove');
  return response;
};
