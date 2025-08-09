import apiClient from './apiClient';
import { AddCommentRequest, UpdateCommentRequest } from '../types/comment/requests';

export const getAllUserComments = async (payload: { page: number; size: number }) => {
  const response = await apiClient.get('/comments/get-user-comments', {
    params: { ...payload },
  });
  return response;
};

export const addComment = async (payload: AddCommentRequest) => {
  const response = await apiClient.post('/comments/add-user-comment', {
    payload,
  });
  return response;
};

export const updateComment = async (payload: UpdateCommentRequest) => {
  const response = await apiClient.post('/comments/update-user-comments', {
    payload,
  });
  return response;
};

export const deleteComment = async (id: number) => {
  const response = await apiClient.delete('/comments/delete-user-comment', {
    params: { id },
  });
  return response;
};
