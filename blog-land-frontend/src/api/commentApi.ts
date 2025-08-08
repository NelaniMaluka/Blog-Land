import apiClient from './apiClient';

export const getAllUserComments = async (page: number, size: number) => {
  const response = await apiClient.get('/comments/get-user-comments', {
    params: { page, size },
  });
  return response;
};

export const addComment = async (postId: number, content: string) => {
  const response = await apiClient.post('/comments/add-user-comment', {
    postId,
    content,
  });
  return response;
};

export const updateComment = async (id: number, postId: number, content: string) => {
  const response = await apiClient.post('/comments/update-user-comments', {
    id,
    postId,
    content,
  });
  return response;
};

export const deleteComment = async (id: number) => {
  const response = await apiClient.delete('/comments/delete-user-comment', {
    params: { id },
  });
  return response;
};
