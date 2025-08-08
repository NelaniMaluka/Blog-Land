import apiClient from './apiClient';
import { Order } from '../types/postType';

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

export const getAllPosts = async (page: number, size: number, order: Order) => {
  const response = await apiClient.get('/post/get/posts', {
    params: { page, size, order },
  });
  return response;
};

export const getTopPosts = async () => {
  const response = await apiClient.get('/post/get/top-post', {});
  return response;
};

export const getLatestPosts = async (page: number, size: number) => {
  const response = await apiClient.get('/post/get/latest-post', {
    params: { page, size },
  });
  return response;
};

export const getTrendingPosts = async (page: number, size: number) => {
  const response = await apiClient.get('/post/get/popular-post', {
    params: { page, size },
  });
  return response;
};

export const getPostsByCategory = async (
  categoryId: number,
  page: number,
  size: number,
  order: Order
) => {
  const response = await apiClient.get('/post/get/category', {
    params: { categoryId, page, size, order },
  });
  return response;
};

export const getAllUserPosts = async (page: number, size: number) => {
  const response = await apiClient.get('/post/get-user-posts', {
    params: { page, size },
  });
  return response;
};

export const addViewToPost = async (id: number) => {
  const response = await apiClient.post(`/post/get/posts/view/${id}`, {});
  return response;
};

export const addPost = async (
  title: string,
  content: string,
  categoryId: number,
  summary: string,
  imgUrl: string,
  draft: boolean,
  scheduledAt: string
) => {
  const response = await apiClient.post('/post/add-user-posts', {
    title,
    content,
    categoryId,
    summary,
    imgUrl,
    draft,
    scheduledAt,
  });
  return response;
};

export const updatePost = async (
  id: number,
  title: string,
  content: string,
  categoryId: number,
  summary: string,
  imgUrl: string,
  draft: boolean,
  scheduledAt: string
) => {
  const response = await apiClient.put('/post/update-user-post', {
    id,
    title,
    content,
    categoryId,
    summary,
    imgUrl,
    draft,
    scheduledAt,
  });
  return response;
};

export const deletePost = async (id: number) => {
  const response = await apiClient.delete('/post/delete-user-post', {
    params: { id },
  });
  return response;
};
