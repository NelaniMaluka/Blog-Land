import apiClient from './apiClient';

export const getPostLikesCount = async (postId: number) => {
  const response = await apiClient.get(`/like/get/post-likes/${postId}`);
  return response;
};

export const getUserLikes = async () => {
  const response = await apiClient.get(`/like/get-user-likes`, {});
  return response;
};

export const addLike = async (postId: number) => {
  const response = await apiClient.post(`/like/add-like/${postId}`, {});
  return response;
};

export const removeLike = async (likeId: number) => {
  const response = await apiClient.delete('/like/remove-like', {
    params: { likeId },
  });
  return response;
};
