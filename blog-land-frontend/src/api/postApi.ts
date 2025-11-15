import apiClient from './apiClient';
import { Order } from '../types/post/response';

export const searchPosts = async (keyword: string) => {
  const response = await apiClient.get('/public/posts/search', {
    params: { keyword },
  });
  return response;
};

export const getRandomPost = async () => {
  const response = await apiClient.get('/public/posts/random');
  return response;
};

export const getRandomPosts = async () => {
  const response = await apiClient.get('/public/posts/related');
  return response;
};

export const getLatestPosts = async (payload: { page: number; size: number }) => {
  const response = await apiClient.get('/public/posts/latest', {
    params: { ...payload },
  });
  return response;
};

export const getTrendingPosts = async (payload: { page: number; size: number }) => {
  const response = await apiClient.get('/public/posts/popular', {
    params: { ...payload },
  });
  return response;
};

export const getPost = async (postId: string) => {
  const response = await apiClient.get(`/public/posts/${postId}`);
  return response;
};

export const getAllPosts = async (payload: { page: number; size: number; order: Order }) => {
  const response = await apiClient.get('/public/posts', {
    params: { ...payload },
  });
  return response;
};

export const addViewToPost = async (postId: string) => {
  const response = await apiClient.post(`/public/posts/${postId}/view`, {});
  return response;
};
