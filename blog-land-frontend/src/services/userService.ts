import { getUserDetails, updateUserDetails, deleteUserDetails } from '../api/userApi';
import { User } from '../types/userType';
import { Provider, ExperienceLevel } from '../types/userType';
import { getAxiosErrorMessage } from '../utils/errorUtils';

export const fetchUser = async (): Promise<User> => {
  try {
    const response = await getUserDetails();
    return response?.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to get user details'));
  }
};

export const updateUser = async (
  firstname: string,
  lastname: string,
  email: string,
  provider: Provider,
  profileIconUrl: string,
  location: string,
  experience: ExperienceLevel,
  socials: Record<string, string>
): Promise<{ message: string }> => {
  try {
    const response = await updateUserDetails(
      firstname,
      lastname,
      email,
      provider,
      profileIconUrl,
      location,
      experience,
      socials
    );
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
