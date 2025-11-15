import he from 'he';
import { addPost, updatePost, deletePost, getAllUserPosts } from '../api/userPostApi';
import { idSchema } from '../schemas/generalSchema';
import { paginationSchema } from '../schemas/paginationSchema';
import { addPostSchema, updatePostSchema } from '../schemas/postSchema';
import { AddPostRequest, UpdatePostRequest } from '../types/post/request';
import { PostResponse } from '../types/post/response';
import { validateOrThrow, getAxiosErrorMessage } from '../utils/errorUtils';
import { stripHtml, formatDate } from '../utils/formatUtils';

export const fetchAllUserPosts = async (payload: { page: number; size: number }): Promise<any> => {
  const validPayload = validateOrThrow(paginationSchema, payload);

  try {
    const response = await getAllUserPosts(validPayload);
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
    throw new Error(getAxiosErrorMessage(error, 'Failed to get user posts'));
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

export const deletePosts = async (id: string): Promise<{ message: string }> => {
  const validPayload = validateOrThrow(idSchema, id);

  try {
    const response = await deletePost(validPayload);
    return response?.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to delete post'));
  }
};
