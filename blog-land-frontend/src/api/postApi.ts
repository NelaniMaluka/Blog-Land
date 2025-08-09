import apiClient from './apiClient';
import { Order } from '../types/post/response';
import { AddPostRequest, UpdatePostRequest } from '../types/post/request';

export const searchPosts = async (keyword: string) => {
  const response = await apiClient.get('/post/api/search', {
    params: { keyword },
  });
  return response;
};

export const getRandomPost = async () => {
  const response = await apiClient.get('/post/get/random-post', {});
  return response;
};

export const getPost = async (id: number) => {
  const response = await apiClient.get(`post/get/post/${id}`, {});
  return response;
};

export const getAllPosts = async (payload: { page: number; size: number; order: Order }) => {
  const response = await apiClient.get('/post/get/posts', {
    params: { ...payload },
  });
  return response;
};

export const getTopPosts = async () => {
  const response = await apiClient.get('/post/get/top-post', {});
  return response;
};

export const getLatestPosts = async (payload: { page: number; size: number }) => {
  const response = await apiClient.get('/post/get/latest-post', {
    params: { ...payload },
  });
  return response;
};

export const getTrendingPosts = async (payload: { page: number; size: number }) => {
  const response = await apiClient.get('/post/get/popular-post', {
    params: { ...payload },
  });
  return response;
};

export const getPostsByCategory = async (payload: {
  categoryId: number;
  page: number;
  size: number;
  order: Order;
}) => {
  const response = await apiClient.get('/post/get/category', {
    params: { ...payload },
  });
  return response;
};

export const getAllUserPosts = async (payload: { page: number; size: number }) => {
  const response = await apiClient.get('/post/get-user-posts', {
    params: { ...payload },
  });
  return response;
};

export const addViewToPost = async (id: number) => {
  const response = await apiClient.post(`/post/get/posts/view/${id}`, {});
  return response;
};

export const addPost = async (payload: AddPostRequest) => {
  const response = await apiClient.post('/post/add-user-posts', {
    payload,
  });
  return response;
};

export const updatePost = async (payload: UpdatePostRequest) => {
  const response = await apiClient.put('/post/update-user-post', {
    payload,
  });
  return response;
};

export const deletePost = async (id: number) => {
  const response = await apiClient.delete('/post/delete-user-post', {
    params: { id },
  });
  return response;
};
