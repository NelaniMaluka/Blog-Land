import { registerUser, loginUser } from '../api/authApi';
import { getAxiosErrorMessage, validateOrThrow } from '../utils/errorUtils';
import { RegisterRequest, LoginRequest } from '../types/auth/requests';
import { registerSchema, loginSchema } from '../schemas/authSchema';

export const createUser = async (payload: RegisterRequest): Promise<string> => {
  const validPayload = validateOrThrow(registerSchema, payload);

  try {
    const response = await registerUser(validPayload);

    if (!response?.data) {
      throw new Error('Login failed: no token received');
    }

    return response?.data;
  } catch (error) {
    throw new Error(
      getAxiosErrorMessage(error, 'Registration failed. Please check your credentials.')
    );
  }
};

export const authenticateUser = async (payload: LoginRequest): Promise<string> => {
  const validPayload = validateOrThrow(loginSchema, payload);

  try {
    const response = await loginUser(validPayload);

    if (!response?.data) {
      throw new Error('Login failed: no token received');
    }

    return response?.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Login failed. Please check your credentials.'));
  }
};

// Google OAuth login
export const oauth = async (): Promise<void> => {
  try {
    window.location.href = 'https://blog-land.onrender.com/oauth2/authorization/google';
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'OAuth login failed.'));
  }
};
