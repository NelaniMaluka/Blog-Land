// src/hooks/useUser.tsx
import { useMutation, useQuery } from '@tanstack/react-query';
import {
  fetchUser,
  fetchPublicUser,
  updateUser,
  deleteUser,
  submitLogoutUser,
  updateUserProfileImg,
  removeUserProfileImg,
  fetchUserSummaryAnalytics,
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
import { useWebSocket } from './useWebSocket';
import { useQueryClient } from '@tanstack/react-query';

export function useGetUser() {
  const queryClient = useQueryClient();
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

  const token = useSelector((state: RootState) => state.auth.jwtToken) ?? undefined;
  useWebSocket(
    '/user/queue/user/update',
    (message) => {
      const raw = JSON.parse(message);
      queryClient.setQueryData(['user'], () => raw);
    },
    token
  );

  return query;
}

export const useGetPublicUser = (nanoId: string | undefined) => {
  const queryClient = useQueryClient();

  const query = useQuery({
    queryKey: ['publicUser', nanoId],
    queryFn: () => fetchPublicUser(nanoId!),
    enabled: !!nanoId,
  });

  useWebSocket(`/queue/user/public-update/${nanoId}`, (message: string) => {
    const updatedUser: UserResponse = JSON.parse(message);
    queryClient.setQueryData(['publicUser', nanoId], updatedUser);
  });

  return query;
};

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
  const { showError } = useSnackbar();

  return useMutation({
    mutationFn: async (payload: { data: UpdateUserRequest }) => {
      const token = await updateUser(payload.data);
      dispatch(setToken(token));
      return token;
    },
    onSuccess: async (token) => {
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

export const useGetUserSumAnalytics = () => {
  return useQuery({
    queryKey: ['userSumAnalytics'],
    queryFn: () => fetchUserSummaryAnalytics(),
  });
};
