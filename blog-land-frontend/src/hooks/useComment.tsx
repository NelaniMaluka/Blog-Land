import { useQuery } from '@tanstack/react-query';
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

export const useGetCommentsCountByPost = (postId: number) => {
  return useQuery({
    queryKey: ['commentsCount', postId],
    queryFn: () => fetchCommentsCountByPost(postId),
  });
};

export const useGetCommentsWithPostId = (payload: {
  postId: number;
  page: number;
  size: number;
}) => {
  return useQuery({
    queryKey: ['Comments', payload],
    queryFn: () => fetchCommentsWithPostId(payload),
  });
};

export const useGetUserCommentsWithPostId = (postId: number) => {
  return useQuery({
    queryKey: ['userComments', postId],
    queryFn: () => fetchUserCommentsWithPostId(postId),
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
