import { useMutation } from '@tanstack/react-query';
import { submitContactForm } from '../services/contactService';

export const useNewsletterSubscription = () => {
  return useMutation({
    mutationFn: submitContactForm,
  });
};
