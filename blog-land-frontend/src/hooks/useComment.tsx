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

export const useGetCommentsCountByPost = (postId: number) => {
  return useQuery({
    queryKey: ['commentsCount', postId],
    queryFn: () => fetchCommentsCountByPost(postId),
  });
};

export const useGetCommentsWithPostId = (
  payload: { postId: number; page: number; size: number },
  options?: Omit<UseQueryOptions<CommentResponse[], Error>, 'queryKey' | 'queryFn'>
) => {
  return useQuery<CommentResponse[], Error>({
    queryKey: ['comments', payload],
    queryFn: () => fetchCommentsWithPostId(payload),
    enabled: !!payload.postId, // don’t run if postId is falsy
    ...options,
  });
};

export const useGetUserCommentsWithPostId = (
  postId: number,
  options?: Omit<UseQueryOptions<UserCommentsResponse[], Error>, 'queryKey' | 'queryFn'>
) => {
  return useQuery<UserCommentsResponse[], Error>({
    queryKey: ['userComments', postId],
    queryFn: () => fetchUserCommentsWithPostId(postId),
    enabled: !!postId, // default guard
    ...options, // safely merge user-provided options
  });
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
  return useMutation({
    mutationFn: updateComments,
  });
};

export const useDeleteComment = () => {
  return useMutation({
    mutationFn: deleteComments,
  });
};
