import apiClient from './apiClient';

export const getPostLikesCount = async (postId: string) => {
  const response = await apiClient.get(`/public/posts/${postId}/likes`);
  return response;
};

export const getUserLikes = async () => {
  const response = await apiClient.get(`/user/posts/likes`);
  return response;
};

export const addLike = async (postId: string) => {
  const response = await apiClient.post(`/user/posts/${postId}/likes`);
  return response;
};

export const removeLike = async (likeId: string) => {
  const response = await apiClient.delete(`/user/posts/likes/${likeId}`);
  return response;
};
