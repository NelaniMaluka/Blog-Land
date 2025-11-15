import { PostResponse, Order } from '../types/post/response';
import {
  searchPosts,
  getRandomPost,
  getRandomPosts,
  getPost,
  getAllPosts,
  getLatestPosts,
  getTrendingPosts,
  addViewToPost,
} from '../api/postApi';
import { getAxiosErrorMessage, validateOrThrow } from '../utils/errorUtils';
import { idSchema } from '../schemas/generalSchema';
import { paginationSchema, paginationSchemaWithOrder } from '../schemas/paginationSchema';
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
    const data = response?.data;

    return {
      ...data,
      title: he.decode(stripHtml(data.title)),
      summary: data.summary ? he.decode(stripHtml(data.summary)) : null,
      createdAt: formatDate(data.createdAt),
    };
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to get random post'));
  }
};

export const fetchRandomPosts = async (): Promise<PostResponse[]> => {
  try {
    const response = await getRandomPosts();
    const data = response?.data;

    return data.map((raw: PostResponse) => ({
      ...raw,
      title: he.decode(stripHtml(raw.title)),
      summary: raw.summary ? he.decode(stripHtml(raw.summary)) : null,
      createdAt: formatDate(raw.createdAt),
    }));
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to get random post'));
  }
};

export const fetchPost = async (id: string): Promise<PostResponse> => {
  const validPayload = validateOrThrow(idSchema, id);

  try {
    const response = await getPost(validPayload);
    const data = response?.data;
    return {
      ...data,
      title: he.decode(stripHtml(data.title)),
      summary: data.summary ? he.decode(stripHtml(data.summary)) : null,
      createdAt: formatDate(data.createdAt),
    };
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

export const fetchLatestPosts = async (payload: {
  page: number;
  size: number;
}): Promise<PostResponse[]> => {
  const validPayload = validateOrThrow(paginationSchema, payload);

  try {
    const response = await getLatestPosts(validPayload);
    const data = response?.data ?? [];

    return data.map(
      (raw: any): PostResponse => ({
        ...raw,
        title: he.decode(stripHtml(raw.title)),
        summary: raw.summary ? he.decode(stripHtml(raw.summary)) : null,
        createdAt: formatDate(raw.createdAt),
      })
    );
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

export const submitView = async (postId: string): Promise<{ message: string }> => {
  const validId = validateOrThrow(idSchema, postId);

  try {
    const response = await addViewToPost(validId);
    return response?.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to submit view'));
  }
};
