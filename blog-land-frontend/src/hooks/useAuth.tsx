// src/hooks/useAuth.tsx
import { useMutation } from '@tanstack/react-query';
import { useDispatch } from 'react-redux';
import { setToken } from '../store/authSlice';
import { createUser, authenticateUser } from '../services/authService';
import { RegisterRequest, LoginRequest } from '../types/auth/requests';
import { useGetUser } from './useUser';
import { ShowSuccessSwal } from '../features/Alerts/SuccessMessage';
import { scheduleLogout } from '../constants/ScheduleLogout';

export function useRegister() {
  const dispatch = useDispatch();
  const { refetch: refetchUser } = useGetUser();

  return useMutation({
    mutationFn: async (payload: RegisterRequest) => {
      const token = await createUser(payload);
      dispatch(setToken(token));
      scheduleLogout(token, dispatch);
      return token;
    },
    onSuccess: async () => {
      const { data } = await refetchUser();
      if (data) {
        ShowSuccessSwal('Sign-up Successful', `Welcome, ${data.firstname} ${data.lastname || ''}!`);
      }
    },
  });
}

export function useLogin() {
  const dispatch = useDispatch();
  const { refetch: refetchUser } = useGetUser();

  return useMutation({
    mutationFn: async (payload: LoginRequest) => {
      const token = await authenticateUser(payload);
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
  });
}

export function useSetOAuthToken() {
  const dispatch = useDispatch();
  const { refetch: refetchUser } = useGetUser();

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
  });
}
