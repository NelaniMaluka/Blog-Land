import { useMutation } from '@tanstack/react-query';
import { useDispatch, UseDispatch } from 'react-redux';
import { setToken } from '../store/authSlice';
import { createUser, authenticateUser } from '../services/authService';
import { RegisterRequest, LoginRequest } from '../types/auth/requests';
import { useGetUser } from './useUser';

export function useRegister() {
  const dispatch = useDispatch();

  return useMutation({
    mutationFn: async (payload: RegisterRequest) => {
      const token = await createUser(payload);
      dispatch(setToken(token));
    },
  });
}

export function useLogin() {
  const dispatch = useDispatch();

  return useMutation({
    mutationFn: async (payload: LoginRequest) => {
      const token = await authenticateUser(payload);
      dispatch(setToken(token));
    },
  });
}
