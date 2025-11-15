import { useMutation } from '@tanstack/react-query';
import { useDispatch } from 'react-redux';
import { logout, setLoginResponse, setToken } from '../store/authSlice';
import { createUser, authenticateUser, submitLogoutUser } from '../services/authService';
import { RegisterRequest, LoginRequest } from '../types/auth/requests';
import { useGetUser } from './useUser';
import { ShowSuccessSwal } from '../features/Alerts/SuccessMessage';
import { scheduleLogout } from '../features/ScheduleLogout';
import { useSnackbar } from '../features/Snackbars/errorMessage';
import { LoginResponse } from '../types/user/response';

export function useRegister() {
  const dispatch = useDispatch();
  const { showError } = useSnackbar();

  return useMutation({
    mutationFn: async (payload: RegisterRequest) => {
      const data = await createUser(payload);
      dispatch(setLoginResponse(data));
      return data;
    },
    onSuccess: async (data: LoginResponse) => {
      scheduleLogout(data.expiresIn, dispatch);
      ShowSuccessSwal(
        'Sign-up Successful',
        `Welcome, ${data.user.firstname} ${data.user.lastname || ''}!`
      );
    },
    onError: (error: any) => {
      const msg = error?.response?.data?.message || error?.message || 'Something went wrong';
      showError(msg);
    },
  });
}

export function useLogin() {
  const dispatch = useDispatch();
  const { showError } = useSnackbar();

  return useMutation({
    mutationFn: async (payload: LoginRequest) => {
      const data = await authenticateUser(payload);
      dispatch(setLoginResponse(data));
      return data;
    },
    onSuccess: async (data: LoginResponse) => {
      scheduleLogout(data.expiresIn, dispatch);
      ShowSuccessSwal(
        'Login Successful',
        `Welcome back, ${data.user.firstname} ${data.user.lastname || ''}!`
      );
    },
    onError: (error: any) => {
      const msg = error?.response?.data?.message || error?.message || 'Something went wrong';
      showError(msg);
    },
  });
}

export const useLogoutUser = () => {
  const dispatch = useDispatch();
  const { showError } = useSnackbar();

  return useMutation({
    mutationFn: async () => {
      const response = await submitLogoutUser();
      return response;
    },
    onSuccess: () => {
      dispatch(logout());
      ShowSuccessSwal('Logout Successful', `We hope to see you again soon!`);
    },
    onError: (error: any) => {
      dispatch(logout());
      const msg = error?.response?.data?.message || error?.message || 'Something went wrong';
      showError(msg);
    },
  });
};

export function useSetOAuthToken() {
  const dispatch = useDispatch();
  const { refetch: refetchUser } = useGetUser();
  const { showError } = useSnackbar();

  return useMutation({
    mutationFn: async (token: string) => {
      dispatch(setToken(token));
      return token;
    },
    onSuccess: async () => {
      const { data } = await refetchUser();
      if (data) {
        scheduleLogout(86400000, dispatch);
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
