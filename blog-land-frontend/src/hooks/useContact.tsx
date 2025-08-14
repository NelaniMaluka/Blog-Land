import { useMutation } from '@tanstack/react-query';
import { submitContactForm } from '../services/contactService';

export const useNewsletterSubscriptiond = () => {
  return useMutation({
    mutationFn: submitContactForm,
  });
};
