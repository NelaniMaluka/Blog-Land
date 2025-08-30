import {
  getUserDetails,
  updateUserDetails,
  deleteUserDetails,
  logoutUser,
  updateProfileIcon,
} from '../api/userApi';
import { UserResponse } from '../types/user/response';
import { getAxiosErrorMessage, validateOrThrow } from '../utils/errorUtils';
import { UpdateUserRequest } from '../types/user/request';
import { updateUserSchema } from '../schemas/userSchema';

export const updateUserProfileImg = async (file: File): Promise<string> => {
  //const validPayload = validateOrThrow(updateUserSchema, payload);

  try {
    const response = await updateProfileIcon(file);
    return response?.data as string;
  } catch (error) {
    throw new Error(
      getAxiosErrorMessage(error, 'Profile image update failed. Could not store file.')
    );
  }
};

export const fetchUser = async (): Promise<UserResponse> => {
  try {
    const response = await getUserDetails();
    return response?.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to get user details'));
  }
};

export const updateUser = async (payload: UpdateUserRequest): Promise<string> => {
  const validPayload = validateOrThrow(updateUserSchema, payload);

  try {
    const response = await updateUserDetails(validPayload);
    return response?.data as string;
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

export const submitLogoutUser = async (): Promise<{ message: string }> => {
  try {
    const response = await logoutUser();
    return response?.data;
  } catch (error) {
    console.log(error);
    throw new Error(getAxiosErrorMessage(error, 'Failed to logout user'));
  }
};
