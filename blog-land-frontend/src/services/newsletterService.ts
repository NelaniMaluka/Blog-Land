import { subscribeToNewsletter } from '../api/newsletterApi';
import { getAxiosErrorMessage, validateOrThrow } from '../utils/errorUtils';
import { emailSchema } from '../schemas/generalSchema';

export const submitNewsletterSubscription = async (email: string): Promise<{ message: string }> => {
  const validPayload = validateOrThrow(emailSchema, { email });

  try {
    const response = await subscribeToNewsletter(validPayload.email);
    return response?.data;
  } catch (error) {
    throw new Error(
      getAxiosErrorMessage(error, 'Newsletter Subscription failed. Please check your email.')
    );
  }
};
