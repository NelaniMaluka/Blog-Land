import { Post, Order, PostWithCategory } from '../types/postType';
import {
  searchPosts,
  getRandomPost,
  getPost,
  getAllPosts,
  getTopPosts,
  getLatestPosts,
  getTrendingPosts,
  getPostsByCategory,
  getAllUserPosts,
  addViewToPost,
  addPost,
  updatePost,
  deletePost,
} from '../api/postApi';
import { getAxiosErrorMessage } from '../utils/errorUtils';

export const fetchSearchedPosts = async (keyword: string): Promise<Post[]> => {
  try {
    const response = await searchPosts(keyword);
    return response?.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to get search results'));
  }
};

export const fetchRandomPost = async (): Promise<Post> => {
  try {
    const response = await getRandomPost();
    return response?.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to get random post'));
  }
};

export const fetchPost = async (id: number): Promise<Post> => {
  try {
    const response = await getPost(id);
    return response?.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to get post'));
  }
};

export const fetchAllPosts = async (page: number, size: number, order: Order): Promise<Post[]> => {
  try {
    const response = await getAllPosts(page, size, order);
    return response?.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed the get all posts'));
  }
};

export const fetchTopPosts = async (): Promise<PostWithCategory[]> => {
  try {
    const response = await getTopPosts();
    return response?.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to get top posts'));
  }
};

export const fetchLatestPosts = async (page: number, size: number): Promise<Post[]> => {
  try {
    const response = await getLatestPosts(page, size);
    return response?.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to get latest post'));
  }
};

export const fetchTrendingPosts = async (page: number, size: number): Promise<Post[]> => {
  try {
    const response = await getTrendingPosts(page, size);
    return response?.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to get trending posts'));
  }
};

export const fetchPostByCategory = async (
  categoryId: number,
  page: number,
  size: number,
  order: Order
): Promise<Post[]> => {
  try {
    const response = await getPostsByCategory(categoryId, page, size, order);
    return response?.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to get category posts'));
  }
};

export const fetchAllUserPosts = async (page: number, size: number): Promise<Post[]> => {
  try {
    const response = await getAllUserPosts(page, size);
    return response?.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to get user posts'));
  }
};

export const submitView = async (id: number): Promise<{ message: string }> => {
  try {
    const response = await addViewToPost(id);
    return response?.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to submit view'));
  }
};

export const submitPost = async (
  title: string,
  content: string,
  categoryId: number,
  summary: string,
  imgUrl: string,
  draft: boolean,
  scheduledAt: string
): Promise<{ message: string }> => {
  try {
    const response = await addPost(title, content, categoryId, summary, imgUrl, draft, scheduledAt);
    return response?.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to submit post'));
  }
};

export const updatePosts = async (
  id: number,
  title: string,
  content: string,
  categoryId: number,
  summary: string,
  imgUrl: string,
  draft: boolean,
  scheduledAt: string
): Promise<{ message: string }> => {
  try {
    const response = await updatePost(
      id,
      title,
      content,
      categoryId,
      summary,
      imgUrl,
      draft,
      scheduledAt
    );
    return response?.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to update posts'));
  }
};

export const deletePosts = async (id: number): Promise<{ message: string }> => {
  try {
    const response = await deletePost(id);
    return response?.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to delete post'));
  }
};
