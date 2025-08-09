import { useMutation } from '@tanstack/react-query';
import { submitNewsletterSubscription } from '../services/newsletterService';

export const useNewsletterSubscription = () => {
  return useMutation({
    mutationFn: submitNewsletterSubscription,
  });
};
