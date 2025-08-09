import { registerUser, loginUser } from '../api/authApi';
import { getAxiosErrorMessage, validateOrThrow } from '../utils/errorUtils';
import { RegisterRequest, LoginRequest } from '../types/auth/requests';
import { registerSchema, loginSchema } from '../schemas/authSchema';

export const createUser = async (payload: RegisterRequest): Promise<{ jwtToken: string }> => {
  const validPayload = validateOrThrow(registerSchema, payload);

  try {
    const response = await registerUser(validPayload);
    return response?.data;
  } catch (error) {
    throw new Error(
      getAxiosErrorMessage(error, 'Registration failed. Please check your credentials.')
    );
  }
};

export const authenticateUser = async (payload: LoginRequest): Promise<{ jwtToken: string }> => {
  const validPayload = validateOrThrow(loginSchema, payload);

  try {
    const response = await loginUser(validPayload);
    return response?.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Login failed. Please check your credentials.'));
  }
};
