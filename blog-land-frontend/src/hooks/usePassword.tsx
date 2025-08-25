import { useMutation } from '@tanstack/react-query';
import {
  submitChangePassword,
  submitForgotPassword,
  submitChangeWithTokenPassword,
} from '../services/changePasswordService';
import { ShowSuccessSwal } from '../features/Alerts/SuccessMessage';

export const useChangePassword = () => {
  return useMutation({
    mutationFn: submitChangePassword,
  });
};

export const useForgotPassword = () => {
  return useMutation({
    mutationFn: async (email: string) => {
      const response = await submitForgotPassword(email);
      return response;
    },
    onSuccess: () => {
      ShowSuccessSwal(
        'Reset Email Sent',
        'A password reset email has been sent to your inbox. Please check your email to proceed.'
      );
    },
  });
};

export const useChangeWithTokenPassword = () => {
  return useMutation({
    mutationFn: submitChangeWithTokenPassword,
  });
};
