import { changePassword, forgotPassword, changePasswordWithToken } from '../api/passwordApi';
import { changePasswordSchema, changePasswordWithTokenSchema } from '../schemas/authSchema';
import { emailSchema } from '../schemas/generalSchema';
import { changePasswordRequest, changePasswordWithTokenRequest } from '../types/password/request';
import { getAxiosErrorMessage, validateOrThrow } from '../utils/errorUtils';

export const submitChangePassword = async (payload: changePasswordRequest): Promise<string> => {
  const validPayload = validateOrThrow(changePasswordSchema, payload);

  try {
    const categories = await changePassword(validPayload);
    return categories.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to submit request'));
  }
};

export const submitForgotPassword = async (email: string): Promise<string> => {
  const validPayload = validateOrThrow(emailSchema, email);

  try {
    const categories = await forgotPassword(validPayload);
    return categories.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to submit request'));
  }
};

export const submitChangeWithTokenPassword = async (
  payload: changePasswordWithTokenRequest
): Promise<string> => {
  const validPayload = validateOrThrow(changePasswordWithTokenSchema, payload);

  try {
    const categories = await changePasswordWithToken(validPayload);
    return categories.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to submit request'));
  }
};
