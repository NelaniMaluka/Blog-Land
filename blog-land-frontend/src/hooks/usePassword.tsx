import { useMutation } from '@tanstack/react-query';
import {
  submitChangePassword,
  submitResetPassword,
  submitRequestPasswordReset,
} from '../services/changePasswordService';
import { ShowSuccessSwal } from '../features/Alerts/SuccessMessage';
import { useSnackbar } from '../features/Snackbars/errorMessage';

export const useChangePassword = () => {
  const { showError } = useSnackbar();

  return useMutation({
    mutationFn: submitChangePassword,
    onError: (error: any) => {
      const msg = error?.response?.data?.message || error?.message || 'Something went wrong';
      showError(msg);
    },
  });
};

export const useForgotPassword = () => {
  const { showError } = useSnackbar();

  return useMutation({
    mutationFn: async (email: string) => {
      const response = await submitRequestPasswordReset(email);
      return response;
    },
    onSuccess: () => {
      ShowSuccessSwal(
        'Reset Email Sent',
        'A password reset email has been sent to your inbox. Please check your email to proceed.'
      );
    },
    onError: (error: any) => {
      const msg = error?.response?.data?.message || error?.message || 'Something went wrong';
      showError(msg);
    },
  });
};

export const useChangeWithTokenPassword = () => {
  const { showError } = useSnackbar();

  return useMutation({
    mutationFn: submitResetPassword,
    onError: (error: any) => {
      const msg = error?.response?.data?.message || error?.message || 'Something went wrong';
      showError(msg);
    },
  });
};
