import { useQuery } from '@tanstack/react-query';
import { useMutation } from '@tanstack/react-query';
import {
  fetchAllUserComments,
  submitComment,
  updateComments,
  deleteComments,
} from '../services/commentService';

export const useGetAllUserComments = (payload: {
  page: number;
  size: number;
  options?: { enabled?: boolean };
}) => {
  const { page, size, options } = payload;

  return useQuery({
    queryKey: ['userComments', payload],
    queryFn: () => fetchAllUserComments({ page, size }),
    enabled: options?.enabled,
  });
};

export const useAddComment = () => {
  return useMutation({
    mutationFn: submitComment,
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
