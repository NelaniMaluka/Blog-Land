import {
  getUserDetails,
  updateUserDetails,
  deleteUserDetails,
  updateProfileIcon,
  removeProfileIcon,
  getPublicUserDetails,
} from '../api/userApi';
import { LoginResponse, UserResponse } from '../types/user/response';
import { getAxiosErrorMessage, validateOrThrow } from '../utils/errorUtils';
import { UpdateUserRequest } from '../types/user/request';
import { updateUserSchema, fileSchema } from '../schemas/userSchema';

export const fetchUser = async (): Promise<UserResponse> => {
  try {
    const response = await getUserDetails();
    return response?.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to get user details'));
  }
};

export const fetchPublicUser = async (nanoId: string): Promise<UserResponse> => {
  try {
    const response = await getPublicUserDetails(nanoId);
    return response?.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to get user details'));
  }
};

export const updateUserProfileImg = async (file: File): Promise<string> => {
  const validPayload = validateOrThrow(fileSchema, file);

  try {
    const response = await updateProfileIcon(validPayload);
    return response?.data as string;
  } catch (error) {
    throw new Error(
      getAxiosErrorMessage(error, 'Profile image update failed. Could not store file.')
    );
  }
};

export const removeUserProfileImg = async (): Promise<string> => {
  try {
    const response = await removeProfileIcon();
    return response?.data as string;
  } catch (error) {
    throw new Error(
      getAxiosErrorMessage(error, 'Profile image removal failed. Could not delete file.')
    );
  }
};

export const updateUser = async (payload: UpdateUserRequest): Promise<LoginResponse> => {
  const validPayload = validateOrThrow(updateUserSchema, payload);

  try {
    const response = await updateUserDetails(validPayload);
    return response?.data;
  } catch (error) {
    throw new Error(
      getAxiosErrorMessage(error, 'User update failed. Please check your credentials.')
    );
  }
};

export const deleteUser = async (): Promise<{ message: string }> => {
  try {
    const response = await deleteUserDetails();
    return response?.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to delete user'));
  }
};
