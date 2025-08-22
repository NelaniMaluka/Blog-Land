import { useMutation } from '@tanstack/react-query';
import { fetchUser, updateUser, deleteUser, submitLogoutUser } from '../services/userService';
import { setUser, logout } from '../store/authSlice';
import { useDispatch, useSelector } from 'react-redux';
import { useQuery, UseQueryOptions } from '@tanstack/react-query';
import { UserResponse } from '../types/user/response';
import { useEffect } from 'react';
import { RootState } from '../store/store';

export function useGetUser(options?: { enabled?: boolean }) {
  const dispatch = useDispatch();
  const isAuthenticated = useSelector((state: RootState) => state.auth.isAuthenticated);

  const query = useQuery<UserResponse, Error>({
    queryKey: ['user'],
    queryFn: fetchUser,
    enabled: isAuthenticated,
  });

  // Use effect to dispatch when data changes
  useEffect(() => {
    if (query.data) {
      dispatch(setUser(query.data));
    }
  }, [query.data, dispatch]);

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
      dispatch(logout());
      const result = await submitLogoutUser();
    },
  });
};
