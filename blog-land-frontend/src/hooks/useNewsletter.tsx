import { useMutation } from '@tanstack/react-query';
import { submitNewsletterSubscription } from '../services/newsletterService';
import { useSnackbar } from '../features/Snackbars/errorMessage';

export const useNewsletterSubscription = () => {
  const { showError } = useSnackbar();

  return useMutation({
    mutationFn: submitNewsletterSubscription,
    onError: (error: any) => {
      const msg = error?.response?.data?.message || error?.message || 'Something went wrong';
      showError(msg);
    },
  });
};
