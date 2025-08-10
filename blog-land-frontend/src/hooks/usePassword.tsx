import { useMutation } from '@tanstack/react-query';
import {
  submitChangePassword,
  submitForgotPassword,
  submitChangeWithTokenPassword,
} from '../services/changePasswordService';

export const useChangePassword = () => {
  return useMutation({
    mutationFn: submitChangePassword,
  });
};

export const useForgotPassword = () => {
  return useMutation({
    mutationFn: submitForgotPassword,
  });
};

export const useChangeWithTokenPassword = () => {
  return useMutation({
    mutationFn: submitChangeWithTokenPassword,
  });
};
