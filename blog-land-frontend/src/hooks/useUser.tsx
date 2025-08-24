// src/hooks/useUser.tsx
import { useMutation, useQuery, UseQueryOptions } from '@tanstack/react-query';
import { fetchUser, updateUser, deleteUser, submitLogoutUser } from '../services/userService';
import { setUser, logout } from '../store/authSlice';
import { useDispatch, useSelector } from 'react-redux';
import { UserResponse } from '../types/user/response';
import { RootState } from '../store/store';
import { ShowSuccessSwal } from '../features/Alerts/SuccessMessage';

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

export const useUpdateUser = () => {
  return useMutation({
    mutationFn: updateUser,
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
  });
};
