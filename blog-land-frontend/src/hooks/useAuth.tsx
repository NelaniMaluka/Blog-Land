// src/hooks/useAuth.tsx
import { useMutation } from '@tanstack/react-query';
import { useDispatch } from 'react-redux';
import { setToken } from '../store/authSlice';
import { createUser, authenticateUser } from '../services/authService';
import { RegisterRequest, LoginRequest } from '../types/auth/requests';
import { useGetUser } from './useUser';
import Swal from 'sweetalert2';

const showSuccessSwal = (title: string, message: string) => {
  Swal.fire({
    icon: 'success',
    title,
    text: message,
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
};

export function useRegister() {
  const dispatch = useDispatch();
  const { refetch: refetchUser } = useGetUser();

  return useMutation({
    mutationFn: async (payload: RegisterRequest) => {
      const token = await createUser(payload);
      dispatch(setToken(token));
      return token;
    },
    onSuccess: async () => {
      const { data } = await refetchUser();
      if (data) {
        showSuccessSwal('Sign-up Successful', `Welcome, ${data.firstname} ${data.lastname || ''}!`);
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
      return token;
    },
    onSuccess: async () => {
      const { data } = await refetchUser();
      if (data) {
        showSuccessSwal(
          'Login Successful',
          `Welcome back, ${data.firstname} ${data.lastname || ''}!`
        );
      }
    },
  });
}
