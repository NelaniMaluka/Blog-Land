import { Order } from '../types/post/response';
import apiClient from './apiClient';

export const getAllCategories = async () => {
  const response = await apiClient.get('/public/posts/categories');
  return response;
};

export const getPostsByCategory = async (
  categoryId: string,
  payload: {
    page: number;
    size: number;
    order: Order;
  }
) => {
  const response = await apiClient.get(`/public/posts/categories/${categoryId}`, {
    params: { ...payload },
  });
  return response;
};
