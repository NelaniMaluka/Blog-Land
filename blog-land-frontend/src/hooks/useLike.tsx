import {
  fetchUserLikes,
  fetchPostLikesCount,
  submitLike,
  deleteLike,
} from '../services/likeService';
import { useQuery } from '@tanstack/react-query';
import { useMutation } from '@tanstack/react-query';
import { useSnackbar } from '../features/Snackbars/errorMessage';
import { RootState, store } from '../store/store';
import { useQueryClient } from '@tanstack/react-query';
import { useWebSocket } from './useWebSocket';
import { useSelector } from 'react-redux';
import { likeResponse } from '../types/like/likeResponse';

export const useGetPostLikesCount = (postId: string) => {
  const queryClient = useQueryClient();

  // React Query for initial fetch
  const query = useQuery({
    queryKey: ['post-likes', postId],
    queryFn: () => fetchPostLikesCount(postId),
  });

  useWebSocket(`/topic/posts/likes/${postId}`, (message) => {
    queryClient.setQueryData(['post-likes', postId], Number(message));
  });

  return query;
};

export const useGetUserLikes = () => {
  const queryClient = useQueryClient();
  const isAuthenticated = store.getState().auth.isAuthenticated;

  const query = useQuery({
    queryKey: ['user-likes'],
    queryFn: () => fetchUserLikes(),
    enabled: isAuthenticated,
  });

  // Subscribe to WebSocket updates for user likes
  if (isAuthenticated) {
    const token = useSelector((state: RootState) => state.auth.jwtToken) ?? undefined;
    useWebSocket(
      '/user/queue/user/posts/likes/update',
      (message) => {
        const updatedLikes: likeResponse[] = JSON.parse(message);
        queryClient.setQueryData(['user-likes'], updatedLikes);
      },
      token
    );
  }

  return query;
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
