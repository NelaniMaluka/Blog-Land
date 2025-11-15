import { useMutation } from '@tanstack/react-query';
import {
  fetchCommentsCountByPost,
  submitComment,
  updateComments,
  deleteComments,
  fetchCommentsWithPostId,
  fetchUserCommentsWithPostId,
} from '../services/commentService';
import { useSnackbar } from '../features/Snackbars/errorMessage';
import { useQuery, UseQueryOptions } from '@tanstack/react-query';
import { UserCommentsResponse } from '../types/comment/response';
import { CommentResponse } from '../types/comment/response';
import { useQueryClient } from '@tanstack/react-query';
import { useWebSocket } from './useWebSocket';
import { formatDate } from '../utils/formatUtils';
import { useSelector } from 'react-redux';
import { RootState } from '../store/store';

export const useGetCommentsCountByPost = (postId: string) => {
  const queryClient = useQueryClient();

  const query = useQuery({
    queryKey: ['commentsCount', postId],
    queryFn: () => fetchCommentsCountByPost(postId),
  });

  useWebSocket(`/topic/posts/comments/count/${postId}`, (message) => {
    queryClient.setQueryData(['commentsCount', postId], Number(message));
  });

  return query;
};

export const useGetCommentsWithPostId = (
  postId: string,
  payload: { page: number; size: number },
  options?: Omit<UseQueryOptions<CommentResponse[], Error>, 'queryKey' | 'queryFn'>
) => {
  const queryClient = useQueryClient();

  const query = useQuery({
    queryKey: ['comments', postId, payload],
    queryFn: () =>
      fetchCommentsWithPostId({
        postId,
        page: payload,
      }),
    enabled: !!postId,
    ...options,
  });

  useWebSocket(`/topic/posts/comments/add/${postId}`, (message) => {
    const raw = JSON.parse(message);
    const newComment: CommentResponse = {
      ...raw,
      createdAt: formatDate(raw.createdAt),
    };
    queryClient.setQueryData(['comments', postId, payload], (old: CommentResponse[] = []) => [
      newComment,
      ...old,
    ]);
  });

  useWebSocket(`/topic/posts/comments/update/${postId}`, (message) => {
    const raw = JSON.parse(message);
    const updatedComment: CommentResponse = {
      ...raw,
      createdAt: formatDate(raw.createdAt),
    };

    queryClient.setQueryData(['comments', postId, payload], (old: CommentResponse[] = []) => {
      return old.map((c) => (c.id === updatedComment.id ? updatedComment : c));
    });
  });

  useWebSocket(`/topic/posts/comments/remove/${postId}`, (message) => {
    const deletedCommentId = JSON.parse(message);

    queryClient.setQueryData(['comments', postId, payload], (old: CommentResponse[] = []) =>
      old.filter((c) => c.id !== deletedCommentId)
    );
  });

  return query;
};

export const useGetUserCommentsWithPostId = (
  postId: string,
  options?: Omit<UseQueryOptions<string[], Error>, 'queryKey' | 'queryFn'>
) => {
  const queryClient = useQueryClient();

  const query = useQuery<string[], Error>({
    queryKey: ['userComments', postId],
    queryFn: () => fetchUserCommentsWithPostId(postId),
    enabled: !!postId,
    ...options,
  });

  const token = useSelector((state: RootState) => state.auth.jwtToken) ?? undefined;
  useWebSocket(
    '/user/queue/posts/comment/add/' + postId,
    (message) => {
      const raw = JSON.parse(message);
      console.log(raw);
      const rawId = typeof raw === 'string' ? raw : raw.id;
      queryClient.setQueryData(['userComments', postId], (old: string[] = []) => [rawId, ...old]);
    },
    token
  );

  useWebSocket(
    '/user/queue/posts/comment/remove/' + postId,
    (message) => {
      const raw = JSON.parse(message);
      const deletedId = typeof raw === 'string' ? raw : raw.id;
      queryClient.setQueryData(['userComments', postId], (old: string[] = []) =>
        old.filter((c) => c !== deletedId)
      );
    },
    token
  );

  return query;
};

export const useAddComment = () => {
  const { showError } = useSnackbar();

  return useMutation({
    mutationFn: submitComment,
    onError: (error: any) => {
      const msg = error?.response?.data?.message || error?.message || 'Something went wrong';
      showError(msg);
    },
  });
};

export const useUpdateComment = () => {
  const { showError } = useSnackbar();

  return useMutation({
    mutationFn: updateComments,
    onError: (error: any) => {
      const msg = error?.response?.data?.message || error?.message || 'Something went wrong';
      showError(msg);
    },
  });
};

export const useDeleteComment = () => {
  const { showError } = useSnackbar();

  return useMutation({
    mutationFn: deleteComments,
    onError: (error: any) => {
      const msg = error?.response?.data?.message || error?.message || 'Something went wrong';
      showError(msg);
    },
  });
};
