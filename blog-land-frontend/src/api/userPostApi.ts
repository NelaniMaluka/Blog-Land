import apiClient from './apiClient';
import { AddPostRequest, UpdatePostRequest } from '../types/post/request';

export const getAllUserPosts = async (payload: { page: number; size: number }) => {
  const response = await apiClient.get('/user/posts', {
    params: { ...payload },
  });
  return response;
};

export const addPost = async (payload: AddPostRequest) => {
  const response = await apiClient.post('/user/posts/add', {
    payload,
  });
  return response;
};

export const updatePost = async (payload: UpdatePostRequest) => {
  const response = await apiClient.put('/user/posts/update', {
    payload,
  });
  return response;
};

export const deletePost = async (postId: string) => {
  const response = await apiClient.delete('/user/posts/remove', {
    params: { postId },
  });
  return response;
};
