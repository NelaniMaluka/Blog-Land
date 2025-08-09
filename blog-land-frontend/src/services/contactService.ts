import { sendContactMessage } from '../api/contactApi';
import { getAxiosErrorMessage, validateOrThrow } from '../utils/errorUtils';
import { AddContactRequest } from '../types/contact/requests';
import { addContectSchema } from '../schemas/contactSchema';

export const submitContactForm = async (
  payload: AddContactRequest
): Promise<{ message: string }> => {
  const validPayload = validateOrThrow(addContectSchema, payload);

  try {
    const response = await sendContactMessage(validPayload);
    return response?.data;
  } catch (error) {
    throw new Error(
      getAxiosErrorMessage(error, 'Form submission failed. Please check your credentials.')
    );
  }
};
