import {
  fetchUserLikes,
  fetchPostLikesCount,
  submitLike,
  deleteLike,
} from '../services/likeService';
import { useQuery } from '@tanstack/react-query';
import { useMutation } from '@tanstack/react-query';
import { useSnackbar } from '../features/Snackbars/errorMessage';
import { store } from '../store/store';

export const useGetPostLikesCount = (postId: number) => {
  return useQuery({
    queryKey: ['postLikes', postId],
    queryFn: () => fetchPostLikesCount(postId),
  });
};

export const useGetUserLikes = () => {
  const isAuthenticated = store.getState().auth.isAuthenticated;

  return useQuery({
    queryKey: ['userLikes'],
    queryFn: () => fetchUserLikes(),
    enabled: isAuthenticated,
  });
};

export const useAddLike = () => {
  const { showError } = useSnackbar();

  return useMutation({
    mutationFn: submitLike,
    onError: (error: any) => {
      const msg = error?.response?.data?.message || error?.message || 'Something went wrong';
      showError(msg);
    },
  });
};

export const useRemoveLike = () => {
  const { showError } = useSnackbar();

  return useMutation({
    mutationFn: deleteLike,
    onError: (error: any) => {
      const msg = error?.response?.data?.message || error?.message || 'Something went wrong';
      showError(msg);
    },
  });
};
