import { getAllUserComments, addComment, updateComment, deleteComment } from '../api/commentApi';
import { Category } from '../types/categoryType';
import { getAxiosErrorMessage } from '../utils/errorUtils';

export const fetchAllUserComments = async (page: number, size: number): Promise<Category> => {
  try {
    const response = await getAllUserComments(page, size);
    return response?.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to get user comments'));
  }
};

export const submitPost = async (postId: number, content: string): Promise<{ message: string }> => {
  try {
    const response = await addComment(postId, content);
    return response?.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to submit post'));
  }
};

export const updateComments = async (
  id: number,
  postId: number,
  content: string
): Promise<{ message: string }> => {
  try {
    const response = await updateComment(id, postId, content);
    return response?.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to update comment'));
  }
};

export const deleteComments = async (id: number): Promise<{ message: string }> => {
  try {
    const response = await deleteComment(id);
    return response?.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to delete comment'));
  }
};
