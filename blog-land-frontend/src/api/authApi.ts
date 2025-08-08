import apiClient from './apiClient';

export const registerUser = async (
  firstname: string,
  lastname: string,
  email: string,
  password: string
) => {
  const response = await apiClient.post('/auth/register', {
    firstname,
    password,
    lastname,
    email,
  });
  return response;
};

export const loginUser = async (email: string, password: string) => {
  const response = await apiClient.post('/auth/login', {
    email,
    password,
  });
  return response;
};
