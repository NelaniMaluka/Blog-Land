import apiClient from './apiClient';
import { UserResponse } from '../types/api';

export const registerUser = async (
  firstname: string,
  lastname: string,
  email: string,
  password: string
): Promise<string> => {
  const response = await apiClient.post<{ token: string }>('/auth/register', {
    firstname,
    password,
    lastname,
    email,
  });
  return response.data.token;
};

export const loginUser = async (email: string, password: string): Promise<string> => {
  const response = await apiClient.post<{ token: string }>('/auth/login', {
    email,
    password,
  });

  return response.data.token;
};

export const getUserDetails = async (): Promise<UserResponse> => {
  const response = await apiClient.get('/user/get-user', {});
  return response.data;
};

export const updateUserDetails = async (
  firstname: string,
  lastname: string,
  email: string,
  provider: string,
  profileIconUrl: string,
  location: string,
  experience: string,
  socials: any
) => {
  const response = await apiClient.put('/user/update-user', {
    firstname,
    lastname,
    email,
    provider,
    profileIconUrl,
    location,
    experience,
    socials,
  });
  return response;
};

export const deleteUserDetails = async () => {
  const response = await apiClient.delete('/user/delete-user', {});
  return response;
};
