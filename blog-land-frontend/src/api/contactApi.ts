import apiClient from './apiClient';

export const sendContactMessage = async (fullName: string, email: string, message: string) => {
  const response = await apiClient.post('/contact-us', {
    fullName,
    email,
    message,
  });
  return response;
};
