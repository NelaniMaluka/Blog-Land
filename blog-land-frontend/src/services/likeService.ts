import { addLike, removeLike, getPostLikesCount, getUserLikes } from '../api/likeApi';
import { idSchema } from '../schemas/generalSchema';
import { likeResponse } from '../types/like/likeResponse';
import { validateOrThrow, getAxiosErrorMessage } from '../utils/errorUtils';
import { formatDigit } from '../utils/formatUtils';

export const fetchPostLikesCount = async (postId: string): Promise<string> => {
  const validPayload = validateOrThrow(idSchema, postId);
  try {
    const response = await getPostLikesCount(validPayload);
    return formatDigit(response?.data);
  } catch (error) {
    throw new Error(
      getAxiosErrorMessage(error, 'Failed to fetch post likes count. Please try again later.')
    );
  }
};

export const fetchUserLikes = async (): Promise<likeResponse[]> => {
  try {
    const response = await getUserLikes();
    return response?.data;
  } catch (error) {
    throw new Error(
      getAxiosErrorMessage(error, 'Failed to fetch your likes. Please try again later.')
    );
  }
};

export const submitLike = async (postId: string): Promise<string> => {
  const validPayload = validateOrThrow(idSchema, postId);

  try {
    const response = await addLike(validPayload);
    return response?.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to submit like. Please try again later.'));
  }
};

export const deleteLike = async (likeId: string): Promise<string> => {
  const validPayload = validateOrThrow(idSchema, likeId);

  try {
    const response = await removeLike(validPayload);
    return response?.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to remove like. Please try again later.'));
  }
};
