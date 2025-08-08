import apiClient from './apiClient';
import { ExperienceLevel, Provider } from '../types/userType';

export const getUserDetails = async () => {
  const response = await apiClient.get('/user/get-user', {});
  return response;
};

export const updateUserDetails = async (
  firstname: string,
  lastname: string,
  email: string,
  provider: Provider,
  profileIconUrl: string,
  location: string,
  experience: ExperienceLevel,
  socials: Record<string, string>
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
