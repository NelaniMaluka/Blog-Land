import { subscribeToNewsletter } from '../api/newsletterApi';
import { getAxiosErrorMessage } from '../utils/errorUtils';

export const submitNewsletterSubscription = async (email: string): Promise<{ message: string }> => {
  try {
    const response = await subscribeToNewsletter(email);
    return response?.data;
  } catch (error) {
    throw new Error(
      getAxiosErrorMessage(error, 'Newsletter Subscription failed. Please check your email.')
    );
  }
};
