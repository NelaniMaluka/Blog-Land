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
import { useQueryClient } from '@tanstack/react-query';
import { useWebSocket } from './useWebSocket';

export const useGetPostLikesCount = (postId: number) => {
  const queryClient = useQueryClient();

  // React Query for initial fetch
  const query = useQuery({
    queryKey: ['postLikes', postId],
    queryFn: () => fetchPostLikesCount(postId),
  });

  useWebSocket(`/topic/like/post-likes/${postId}`, (message) => {
    queryClient.setQueryData(['postLikes', postId], Number(message));
  });

  return query;
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
