import apiClient from './apiClient';
import { CommentRequest } from '../types/comment/requests';

export const getCommentsCountByPost = async (postId: string) => {
  const response = await apiClient.get(`/public/posts/${postId}/comments/count`);
  return response;
};

export const getCommentsByPost = async (
  postId: string,
  payload: {
    page: number;
    size: number;
  }
) => {
  const response = await apiClient.get(`/public/posts/${postId}/comments`, {
    params: { ...payload },
  });
  return response;
};

export const getUserCommentsByPost = async (postId: string) => {
  const response = await apiClient.get(`/user/posts/${postId}/comments/ids`);
  return response;
};

export const addComment = async (postId: string, payload: CommentRequest) => {
  const response = await apiClient.post(`/user/posts/${postId}/comments`, payload);
  return response;
};

export const updateComment = async (postId: string, commentId: string, payload: CommentRequest) => {
  const response = await apiClient.put(`/user/posts/${postId}/comments/${commentId}`, payload);
  return response;
};

export const deleteComment = async (commentId: string) => {
  const response = await apiClient.delete(`/user/posts/comments/${commentId}`);
  return response;
};
