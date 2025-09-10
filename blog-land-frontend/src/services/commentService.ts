import {
  getCommentsCountByPost,
  getCommentsByPost,
  getUserCommentsByPost,
  addComment,
  updateComment,
  deleteComment,
} from '../api/commentApi';
import { getAxiosErrorMessage, validateOrThrow } from '../utils/errorUtils';
import { paginationSchemaWithId } from '../schemas/paginationSchema';
import { addCommentSchema, updateCommentSchema } from '../schemas/commentSchema';
import { AddCommentRequest } from '../types/comment/requests';
import { UpdateCommentRequest } from '../types/comment/requests';
import { idSchema } from '../schemas/generalSchema';
import { CommentResponse, UserCommentsResponse } from '../types/comment/response';
import { formatDate } from '../utils/formatUtils';

export const fetchCommentsCountByPost = async (id: number): Promise<number> => {
  const validPayload = validateOrThrow(idSchema, { id });
  try {
    const response = await getCommentsCountByPost(validPayload.id);
    return response?.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to get comments count'));
  }
};

export const fetchCommentsWithPostId = async (payload: {
  postId: number;
  page: number;
  size: number;
}): Promise<CommentResponse[]> => {
  const validPayload = validateOrThrow(paginationSchemaWithId, payload);

  try {
    const response = await getCommentsByPost(validPayload);
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

export const fetchUserCommentsWithPostId = async (id: number): Promise<UserCommentsResponse[]> => {
  const validPayload = validateOrThrow(idSchema, { id });

  try {
    const response = await getUserCommentsByPost(validPayload.id);
    return response.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to get user comments'));
  }
};

export const submitComment = async (payload: AddCommentRequest): Promise<{ message: string }> => {
  const validPayload = validateOrThrow(addCommentSchema, payload);
  try {
    const response = await addComment(validPayload);
    return response?.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to submit post'));
  }
};

export const updateComments = async (
  payload: UpdateCommentRequest
): Promise<{ message: string }> => {
  const validPayload = validateOrThrow(updateCommentSchema, payload);

  try {
    const response = await updateComment(validPayload);
    return response?.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to update comment'));
  }
};

export const deleteComments = async (id: number): Promise<{ message: string }> => {
  const validPayload = validateOrThrow(idSchema, id);

  try {
    const response = await deleteComment(validPayload.id);
    return response?.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to delete comment'));
  }
};
