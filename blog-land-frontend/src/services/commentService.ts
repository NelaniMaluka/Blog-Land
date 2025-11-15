import {
  getCommentsCountByPost,
  getCommentsByPost,
  getUserCommentsByPost,
  addComment,
  updateComment,
  deleteComment,
} from '../api/commentApi';
import { getAxiosErrorMessage, validateOrThrow } from '../utils/errorUtils';
import { commentSchema } from '../schemas/commentSchema';
import { CommentRequest } from '../types/comment/requests';
import { idSchema } from '../schemas/generalSchema';
import { CommentResponse, UserCommentsResponse } from '../types/comment/response';
import { formatDate } from '../utils/formatUtils';
import { paginationSchema } from '../schemas/paginationSchema';

export const fetchCommentsCountByPost = async (id: string): Promise<number> => {
  const validPayload = validateOrThrow(idSchema, id);
  try {
    const response = await getCommentsCountByPost(validPayload);
    return response?.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to get comments count'));
  }
};

export const fetchCommentsWithPostId = async (payload: {
  postId: string;
  page: {
    page: number;
    size: number;
  };
}): Promise<CommentResponse[]> => {
  const validPayload = validateOrThrow(paginationSchema, payload.page);
  const validId = validateOrThrow(idSchema, payload.postId);

  try {
    const response = await getCommentsByPost(validId, validPayload);
    let data = response?.data?.content;
    const formattedData = (data ?? []).map((raw: CommentResponse) => ({
      ...raw,
      createdAt: formatDate(raw.createdAt),
    }));

    return formattedData;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to get user comments'));
  }
};

export const fetchUserCommentsWithPostId = async (id: string): Promise<string[]> => {
  const validPayload = validateOrThrow(idSchema, id);

  try {
    const response = await getUserCommentsByPost(validPayload);
    return response.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to get user comments'));
  }
};

export const submitComment = async (payload: {
  postId: string;
  data: CommentRequest;
}): Promise<{ message: string }> => {
  const validPayload = validateOrThrow(commentSchema, payload.data);
  const validId = validateOrThrow(idSchema, payload.postId);
  try {
    const response = await addComment(validId, validPayload);
    return response?.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to submit post'));
  }
};

export const updateComments = async (payload: {
  postId: string;
  commentId: string;
  data: CommentRequest;
}): Promise<{ message: string }> => {
  const validPayload = validateOrThrow(commentSchema, payload.data);
  const validPostId = validateOrThrow(idSchema, payload.postId);
  const validCommentId = validateOrThrow(idSchema, payload.commentId);
  try {
    const response = await updateComment(validPostId, validCommentId, validPayload);
    return response?.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to update comment'));
  }
};

export const deleteComments = async (commentId: string): Promise<{ message: string }> => {
  const validPayload = validateOrThrow(idSchema, commentId);
  try {
    const response = await deleteComment(validPayload);
    return response?.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to delete comment'));
  }
};
