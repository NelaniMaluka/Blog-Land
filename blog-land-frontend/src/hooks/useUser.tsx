// src/hooks/useUser.tsx
import { useMutation, useQuery, UseQueryOptions } from '@tanstack/react-query';
import {
  fetchUser,
  updateUser,
  deleteUser,
  submitLogoutUser,
  updateUserProfileImg,
  removeUserProfileImg,
} from '../services/userService';
import { setUser, logout, setToken } from '../store/authSlice';
import { useDispatch, useSelector } from 'react-redux';
import { UserResponse } from '../types/user/response';
import { RootState } from '../store/store';
import { ShowSuccessSwal } from '../features/Alerts/SuccessMessage';
import { UpdateUserRequest } from '../types/user/request';
import { scheduleLogout } from '../features/ScheduleLogout';
import { useSnackbar } from '../features/Snackbars/errorMessage';
import { useEffect } from 'react';

export function useGetUser() {
  const dispatch = useDispatch();
  const isAuthenticated = useSelector((state: RootState) => state.auth.isAuthenticated);

  const query = useQuery<UserResponse, Error, UserResponse>({
    queryKey: ['user'],
    queryFn: fetchUser,
    enabled: isAuthenticated,
  });

  // Handle success case with useEffect
  useEffect(() => {
    if (query.data) {
      dispatch(setUser(query.data));
    }
  }, [query.data, dispatch]);

  return query;
}

export const useUpdateProfileImage = () => {
  const { showError } = useSnackbar();

  return useMutation({
    mutationFn: updateUserProfileImg,
    onError: (error: any) => {
      const msg = error?.response?.data?.message || error?.message || 'Something went wrong';
      showError(msg);
    },
  });
};

export const useRemoveProfileImage = () => {
  const { showError } = useSnackbar();

  return useMutation({
    mutationFn: removeUserProfileImg,
    onError: (error: any) => {
      const msg = error?.response?.data?.message || error?.message || 'Something went wrong';
      showError(msg);
    },
  });
};

export const useUpdateUser = () => {
  const dispatch = useDispatch();
  const { refetch: refetchUser } = useGetUser();
  const { showError } = useSnackbar();

  return useMutation({
    mutationFn: async (payload: { data: UpdateUserRequest }) => {
      const token = await updateUser(payload.data);
      dispatch(setToken(token));
      return token;
    },
    onSuccess: async (token) => {
      const { data } = await refetchUser();
      scheduleLogout(token, dispatch);
      ShowSuccessSwal('Profile Updated', `Your profile was successfully updated!`);
    },
    onError: (error: any) => {
      const msg = error?.response?.data?.message || error?.message || 'Something went wrong';
      showError(msg);
    },
  });
};

export const useDeleteUser = () => {
  const { showError } = useSnackbar();

  return useMutation({
    mutationFn: deleteUser,
    onError: (error: any) => {
      const msg = error?.response?.data?.message || error?.message || 'Something went wrong';
      showError(msg);
    },
  });
};

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
