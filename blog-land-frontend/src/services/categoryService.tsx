import apiClient from './apiClient';

export const getAllCategories = async () => {
  const response = await apiClient.get('/category', {});
  return response;
};
