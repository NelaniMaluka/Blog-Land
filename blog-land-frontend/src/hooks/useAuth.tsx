// src/hooks/useAuth.tsx
import { useMutation } from '@tanstack/react-query';
import { useDispatch } from 'react-redux';
import { setToken } from '../store/authSlice';
import { createUser, authenticateUser } from '../services/authService';
import { RegisterRequest, LoginRequest } from '../types/auth/requests';
import { useGetUser } from './useUser';
import { ShowSuccessSwal } from '../features/Alerts/SuccessMessage';
import { scheduleLogout } from '../features/ScheduleLogout';
import { useSnackbar } from '../features/Snackbars/errorMessage';

export function useRegister() {
  const dispatch = useDispatch();
  const { refetch: refetchUser } = useGetUser();
  const { showError } = useSnackbar();

  return useMutation({
    mutationFn: async (payload: RegisterRequest) => {
      const token = await createUser(payload);
      dispatch(setToken(token));

      return token;
    },
    onSuccess: async (token) => {
      const { data } = await refetchUser();
      scheduleLogout(token, dispatch);
      if (data) {
        ShowSuccessSwal('Sign-up Successful', `Welcome, ${data.firstname} ${data.lastname || ''}!`);
      }
    },
    onError: (error: any) => {
      const msg = error?.response?.data?.message || error?.message || 'Something went wrong';
      showError(msg);
    },
  });
}

export function useLogin() {
  const dispatch = useDispatch();
  const { refetch: refetchUser } = useGetUser();
  const { showError } = useSnackbar();

  return useMutation({
    mutationFn: async (payload: LoginRequest) => {
      const token = await authenticateUser(payload);
      dispatch(setToken(token));
      scheduleLogout(token, dispatch);
      return token;
    },
    onSuccess: async (token) => {
      const { data } = await refetchUser();
      scheduleLogout(token, dispatch);
      if (data) {
        ShowSuccessSwal(
          'Login Successful',
          `Welcome back, ${data.firstname} ${data.lastname || ''}!`
        );
      }
    },
    onError: (error: any) => {
      const msg = error?.response?.data?.message || error?.message || 'Something went wrong';
      showError(msg);
    },
  });
}

export function useSetOAuthToken() {
  const dispatch = useDispatch();
  const { refetch: refetchUser } = useGetUser();
  const { showError } = useSnackbar();

  return useMutation({
    mutationFn: async (token: string) => {
      dispatch(setToken(token));
      scheduleLogout(token, dispatch);
      return token;
    },
    onSuccess: async () => {
      const { data } = await refetchUser();
      if (data) {
        ShowSuccessSwal(
          'Login Successful',
          `Welcome back, ${data.firstname} ${data.lastname || ''}!`
        );
      }
    },
    onError: (error: any) => {
      const msg = error?.response?.data?.message || error?.message || 'Something went wrong';
      showError(msg);
    },
  });
}
