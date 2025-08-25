import { changePassword, forgotPassword, changePasswordWithToken } from '../api/passwordApi';
import { changePasswordSchema, changePasswordWithTokenSchema } from '../schemas/authSchema';
import { emailSchema } from '../schemas/generalSchema';
import { changePasswordRequest, changePasswordWithTokenRequest } from '../types/password/request';
import { getAxiosErrorMessage, validateOrThrow } from '../utils/errorUtils';

export const submitChangePassword = async (payload: changePasswordRequest): Promise<string> => {
  const validPayload = validateOrThrow(changePasswordSchema, payload);

  try {
    const response = await changePassword(validPayload);
    return response.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to submit request'));
  }
};

export const submitForgotPassword = async (email: string): Promise<string> => {
  const validPayload = validateOrThrow(emailSchema, { email });

  try {
    const response = await forgotPassword(validPayload);
    console.log(response);
    return response.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to submit request'));
  }
};

export const submitChangeWithTokenPassword = async (
  payload: changePasswordWithTokenRequest
): Promise<string> => {
  const validPayload = validateOrThrow(changePasswordWithTokenSchema, payload);

  try {
    const response = await changePasswordWithToken(validPayload);
    return response.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to submit request'));
  }
};
