import { sendContactMessage } from '../api/contactApi';
import { getAxiosErrorMessage } from '../utils/errorUtils';

export const submitContactForm = async (
  fullname: string,
  email: string,
  message: string
): Promise<{ message: string }> => {
  try {
    const response = await sendContactMessage(fullname, email, message);
    return response?.data;
  } catch (error) {
    throw new Error(
      getAxiosErrorMessage(error, 'Form submission failed. Please check your credentials.')
    );
  }
};
