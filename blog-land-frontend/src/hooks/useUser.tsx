import { useMutation } from '@tanstack/react-query';
import { fetchUser, updateUser, deleteUser, submitLogoutUser } from '../services/userService';
import { setUser, logout } from '../store/authSlice';
import { useDispatch, UseDispatch } from 'react-redux';

export function useGetUser() {
  const dispatch = useDispatch();

  return useMutation({
    mutationFn: async () => {
      const user = await fetchUser();

      dispatch(setUser(user));
    },
  });
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
      const result = await submitLogoutUser();

      dispatch(logout());
    },
  });
};
