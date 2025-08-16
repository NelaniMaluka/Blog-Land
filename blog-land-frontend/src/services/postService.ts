import { PostResponse, Order, PostWithCategoryResponse } from '../types/post/response';
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
import { getAxiosErrorMessage, validateOrThrow } from '../utils/errorUtils';
import { idSchema } from '../schemas/generalSchema';
import {
  paginationSchema,
  paginationSchemaWithOrder,
  paginationWithCategoryIdSchema,
} from '../schemas/paginationSchema';
import { AddPostRequest, UpdatePostRequest } from '../types/post/request';
import { addPostSchema, updatePostSchema } from '../schemas/postSchema';
import { stripHtml, formatDate } from '../utils/formatUtils';
import he from 'he';
import { PaginatedPosts } from '../types/post/response';

export const fetchSearchedPosts = async (keyword: string): Promise<PostResponse[]> => {
  try {
    const response = await searchPosts(keyword);
    return response?.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to get search results'));
  }
};

export const fetchRandomPost = async (): Promise<PostResponse> => {
  try {
    const response = await getRandomPost();
    return response?.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to get random post'));
  }
};

export const fetchPost = async (id: number): Promise<PostResponse> => {
  const validPayload = validateOrThrow(idSchema, id);

  try {
    const response = await getPost(validPayload.id);
    return response?.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to get post'));
  }
};

export const fetchAllPosts = async (payload: {
  page: number;
  size: number;
  order?: Order;
}): Promise<PaginatedPosts> => {
  const validPayload = validateOrThrow(paginationSchemaWithOrder, payload);

  try {
    const response = await getAllPosts(validPayload);
    const data = response?.data;

    return {
      ...data,
      content: (data?.content ?? []).map((raw: PostResponse) => ({
        ...raw,
        title: he.decode(stripHtml(raw.title)),
        summary: raw.summary ? he.decode(stripHtml(raw.summary)) : null,
        createdAt: formatDate(raw.createdAt),
      })),
    };
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to get all posts'));
  }
};

export const fetchTopPosts = async (): Promise<PostWithCategoryResponse[]> => {
  try {
    const response = await getTopPosts();
    return response?.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to get top posts'));
  }
};

export const fetchLatestPosts = async (payload: {
  page: number;
  size: number;
}): Promise<PostResponse[]> => {
  const validPayload = validateOrThrow(paginationSchema, payload);

  try {
    const response = await getLatestPosts(validPayload);
    return response?.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to get latest post'));
  }
};

export const fetchTrendingPosts = async (payload: {
  page: number;
  size: number;
}): Promise<PaginatedPosts> => {
  const validPayload = validateOrThrow(paginationSchema, payload);

  try {
    const response = await getTrendingPosts(validPayload);
    const data = response?.data;

    return {
      ...data,
      content: (data?.content ?? []).map((raw: PostResponse) => ({
        ...raw,
        title: he.decode(stripHtml(raw.title)),
        summary: raw.summary ? he.decode(stripHtml(raw.summary)) : null,
        createdAt: formatDate(raw.createdAt),
      })),
    };
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to get trending posts'));
  }
};

export const fetchPostByCategory = async (payload: {
  categoryId: number;
  page: number;
  size: number;
  order: Order;
}): Promise<PaginatedPosts> => {
  const validPayload = validateOrThrow(paginationWithCategoryIdSchema, payload);

  try {
    const response = await getPostsByCategory(validPayload);
    const data = response?.data;

    return {
      ...data,
      content: (data?.content ?? []).map((raw: PostResponse) => ({
        ...raw,
        title: he.decode(stripHtml(raw.title)),
        summary: raw.summary ? he.decode(stripHtml(raw.summary)) : null,
        createdAt: formatDate(raw.createdAt),
      })),
    };
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to get category posts'));
  }
};

export const fetchAllUserPosts = async (payload: {
  page: number;
  size: number;
}): Promise<PostResponse[]> => {
  const validPayload = validateOrThrow(paginationSchema, payload);

  try {
    const response = await getAllUserPosts(validPayload);
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

export const submitPost = async (payload: AddPostRequest): Promise<{ message: string }> => {
  const validPayload = validateOrThrow(addPostSchema, payload);

  try {
    const response = await addPost(validPayload);
    return response?.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to submit post'));
  }
};

export const updatePosts = async (payload: UpdatePostRequest): Promise<{ message: string }> => {
  const validPayload = validateOrThrow(updatePostSchema, payload);

  try {
    const response = await updatePost(validPayload);
    return response?.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to update posts'));
  }
};

export const deletePosts = async (id: number): Promise<{ message: string }> => {
  const validPayload = validateOrThrow(idSchema, id);

  try {
    const response = await deletePost(validPayload.id);
    return response?.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to delete post'));
  }
};
