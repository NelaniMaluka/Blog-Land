import { addLike, removeLike, getPostLikesCount, getUserLikes } from '../api/likeApi';
import { idSchema } from '../schemas/generalSchema';
import { likeResponse } from '../types/like/likeResponse';
import { validateOrThrow, getAxiosErrorMessage } from '../utils/errorUtils';
import { formatDigit } from '../utils/formatUtils';

export const fetchPostLikesCount = async (id: number): Promise<string> => {
  const validPayload = validateOrThrow(idSchema, { id });
  try {
    const response = await getPostLikesCount(validPayload.id);
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

export const submitLike = async (id: number): Promise<string> => {
  const validPayload = validateOrThrow(idSchema, { id });

  try {
    const response = await addLike(validPayload.id);
    return response?.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to submit like. Please try again later.'));
  }
};

export const deleteLike = async (id: number): Promise<string> => {
  const validPayload = validateOrThrow(idSchema, { id });

  try {
    const response = await removeLike(validPayload.id);
    return response?.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to remove like. Please try again later.'));
  }
};
