import { changePassword, resetPassword, requestPasswordReset } from '../api/passwordApi';
import { changePasswordSchema, forgotPasswordSchema } from '../schemas/authSchema';
import { emailSchema } from '../schemas/generalSchema';
import { changePasswordRequest, forgotPasswordRequest } from '../types/password/request';
import { getAxiosErrorMessage, validateOrThrow } from '../utils/errorUtils';

export const submitRequestPasswordReset = async (email: string): Promise<string> => {
  const validPayload = validateOrThrow(emailSchema, email);

  try {
    const response = await requestPasswordReset(validPayload);
    return response.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to submit request'));
  }
};

export const submitResetPassword = async (payload: {
  token: string;
  data: forgotPasswordRequest;
}): Promise<string> => {
  const validPayload = validateOrThrow(forgotPasswordSchema, payload.data);

  try {
    const response = await resetPassword(payload.token, validPayload);
    return response.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to submit request'));
  }
};

export const submitChangePassword = async (payload: changePasswordRequest): Promise<string> => {
  const validPayload = validateOrThrow(changePasswordSchema, payload);

  try {
    const response = await changePassword(validPayload);
    return response.data;
  } catch (error) {
    throw new Error(getAxiosErrorMessage(error, 'Failed to submit request'));
  }
};
