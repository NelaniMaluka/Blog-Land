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
import { scheduleLogout } from '../constants/ScheduleLogout';

export function useGetUser(options?: { enabled?: boolean }) {
  const dispatch = useDispatch();
  const isAuthenticated = useSelector((state: RootState) => state.auth.isAuthenticated);

  const query = useQuery<UserResponse, Error>({
    queryKey: ['user'],
    queryFn: fetchUser,
    enabled: isAuthenticated,
    onSuccess: (data: UserResponse) => {
      dispatch(setUser(data));
    },
    ...options,
  } as UseQueryOptions<UserResponse, Error, UserResponse, readonly unknown[]>);

  return query;
}

export const useUpdateProfileImage = () => {
  return useMutation({
    mutationFn: updateUserProfileImg,
  });
};

export const useRemoveProfileImage = () => {
  return useMutation({
    mutationFn: removeUserProfileImg,
  });
};

export const useUpdateUser = () => {
  const dispatch = useDispatch();
  const { refetch: refetchUser } = useGetUser();

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
  });
};

export const useDeleteUser = () => {
  return useMutation({
    mutationFn: deleteUser,
  });
};

export const useLogoutUser = () => {
  const dispatch = useDispatch();

  return useMutation({
    mutationFn: async () => {
      const response = await submitLogoutUser();
      return response;
    },
    onSuccess: () => {
      dispatch(logout());
      ShowSuccessSwal('Logout Successful', `We hope to see you again soon!`);
    },
    onError: () => {
      dispatch(logout());
    },
  });
};
