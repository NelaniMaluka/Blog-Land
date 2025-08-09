import { getAllUserComments, addComment, updateComment, deleteComment } from '../api/commentApi';
import { CategoryResponse } from '../types/category/response';
import { getAxiosErrorMessage, validateOrThrow } from '../utils/errorUtils';
import { paginationSchema } from '../schemas/paginationSchema';
import { addCommentSchema, updateCommentSchema } from '../schemas/commentSchema';
import { AddCommentRequest } from '../types/comment/requests';
import { UpdateCommentRequest } from '../types/comment/requests';
import { idSchema } from '../schemas/generalSchema';

export const fetchAllUserComments = async (payload: {
  page: number;
  size: number;
}): Promise<CategoryResponse> => {
  const validPayload = validateOrThrow(paginationSchema, payload);

  try {
    const response = await getAllUserComments(validPayload);
    return response?.data;
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
