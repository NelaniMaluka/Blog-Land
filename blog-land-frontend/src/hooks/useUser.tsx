// src/hooks/useUser.tsx
import { useMutation, useQuery, UseQueryOptions } from '@tanstack/react-query';
import { fetchUser, updateUser, deleteUser, submitLogoutUser } from '../services/userService';
import { setUser, logout } from '../store/authSlice';
import { useDispatch, useSelector } from 'react-redux';
import { UserResponse } from '../types/user/response';
import { RootState } from '../store/store';
import Swal from 'sweetalert2';

export function useGetUser(options?: { enabled?: boolean }) {
  const dispatch = useDispatch();
  const isAuthenticated = useSelector((state: RootState) => state.auth.isAuthenticated);

  const query = useQuery<UserResponse, Error>({
    queryKey: ['user'], // just a string array, no need for as const
    queryFn: fetchUser,
    enabled: isAuthenticated,
    onSuccess: (data: UserResponse) => {
      dispatch(setUser(data));

      Swal.fire({
        icon: 'success',
        title: 'User Loaded',
        text: `Welcome, ${data.firstname} ${data.lastname || ''}!`,
        timer: 2500,
        showConfirmButton: false,
        position: 'top-end',
        background: '#fff',
        iconColor: '#4caf50',
        customClass: {
          popup: 'swal-popup-small',
          title: 'swal-title-small',
        },
      });
    },
    ...options,
  } as UseQueryOptions<UserResponse, Error, UserResponse, readonly unknown[]>);

  return query;
}

// Mutation hooks for user management
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
    },
  });
};
