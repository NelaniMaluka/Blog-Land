import { useMutation } from '@tanstack/react-query';
import { submitContactForm } from '../services/contactService';
import { useSnackbar } from '../features/Snackbars/errorMessage';

export const useNewsletterSubscriptiond = () => {
  const { showError } = useSnackbar();

  return useMutation({
    mutationFn: submitContactForm,
    onError: (error: any) => {
      const msg = error?.response?.data?.message || error?.message || 'Something went wrong';
      showError(msg);
    },
  });
};
