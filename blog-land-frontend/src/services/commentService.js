import apiClient from "./apiClient";

export const getAllComments = async (page, size) => {
  const response = await apiClient.get("/comments/get/comments", {
    params: { page, size },
  });
  return response;
};

export const getAllCommentsByPost = async (postId, page, size) => {
  const response = await apiClient.get("/comments/get/comments", {
    params: { postId, page, size },
  });
  return response;
};

export const getAllUserComments = async (page, size) => {
  const response = await apiClient.get("/comments/get-user-comments", {
    params: { page, size },
  });
  return response;
};

export const addComment = async (postId, content) => {
  const response = await apiClient.post("/comments/add-user-comment", {
    postId,
    content,
  });
  return response;
};

export const updateComment = async (id, postId, content) => {
  const response = await apiClient.post("/comments/update-user-comments", {
    id,
    postId,
    content,
  });
  return response;
};

export const deleteComment = async (id) => {
  const response = await apiClient.post("/comments/delete-user-comment", {
    id,
  });
  return response;
};
