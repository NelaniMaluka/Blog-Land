import apiClient from "./apiClient";

export const searchPosts = async (query) => {
  const response = await apiClient.get("/post/api/search", {
    params: { query },
  });
  return response;
};

export const getRandomPost = async () => {
  const response = await apiClient.get("/post/get/random-post", {});
  return response;
};

export const getPost = async (id) => {
  const response = await apiClient.get(`post/get/post/${id}`, {});
  return response;
};

export const getAllPosts = async (page, size, order) => {
  const response = await apiClient.get("/post/get/posts", {
    params: { page, size, order },
  });
  return response;
};

export const getTopPosts = async () => {
  const response = await apiClient.get("/post/get/top-post", {});
  return response;
};

export const getLatestPosts = async (page, size) => {
  const response = await apiClient.get("/post/get/latest-post", {
    params: { page, size },
  });
  return response;
};

export const getTrendingPosts = async (page, size) => {
  const response = await apiClient.get("/post/get/popular-post", {
    params: { page, size },
  });
  return response;
};

export const getPostsByCategory = async (categoryId, page, size, order) => {
  const response = await apiClient.get("/post/get/category", {
    params: { categoryId, page, size, order },
  });
  return response;
};

export const getAllUserPosts = async (page, size) => {
  const response = await apiClient.get("/post/get/category", {
    params: { page, size },
  });
  return response;
};

export const addViewToPost = async (id) => {
  const response = await apiClient.post(`/post/get/posts/view/${id}`, {});
  return response;
};

export const addPost = async (
  title,
  content,
  categoryId,
  summary,
  imgUrl,
  draft,
  scheduledAt
) => {
  const response = await apiClient.post("/post/add-user-posts", {
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
  id,
  title,
  content,
  categoryId,
  summary,
  imgUrl,
  draft,
  scheduledAt
) => {
  const response = await apiClient.put("/post/update-user-post", {
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

export const deletePost = async (id) => {
  const response = await apiClient.delete("/post/update-user-post", {
    id,
  });
  return response;
};
