import { registerUser, loginUser } from '../api/authApi';
import { getAxiosErrorMessage } from '../utils/errorUtils';

export const createUser = async (
  firstname: string,
  lastname: string,
  email: string,
  password: string
): Promise<{ jwtToken: string }> => {
  try {
    const response = await registerUser(firstname, lastname, email, password);
    return response?.data;
  } catch (error) {
    throw new Error(
      getAxiosErrorMessage(error, 'Registration failed. Please check your credentials.')
    );
  }
};

export const authenticateUser = async (
  email: string,
  password: string
): Promise<{ jwtToken: string }> => {
  try {
    const response = await loginUser(email, password);
    return response?.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Login failed. Please check your credentials.'));
  }
};
