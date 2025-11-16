import apiClient from './apiClient';

export const subscribeToNewsletter = async (email: string) => {
  const response = await apiClient.post(`/public/newsletter`, null, {
    params: { email },
  });
  return response.data;
};
