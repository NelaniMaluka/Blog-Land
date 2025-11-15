import apiClient from './apiClient';

export const subscribeToNewsletter = async (email: string) => {
  const response = await apiClient.post(`/public/newsletter`, { email });
  return response;
};
