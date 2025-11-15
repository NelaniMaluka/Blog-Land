import { useQuery, useMutation } from '@tanstack/react-query';
import { useSnackbar } from '../features/Snackbars/errorMessage';
import {
  fetchAllUserPosts,
  submitPost,
  updatePosts,
  deletePosts,
} from '../services/userPostService';

export const useGetAllUserPost = (payload: {
  page: number;
  size: number;
  options?: { enabled?: boolean };
}) => {
  const { page, size, options } = payload;

  return useQuery({
    queryKey: ['userPosts', page, size],
    queryFn: () => fetchAllUserPosts({ page, size }),
    enabled: options?.enabled ?? true,
  });
};

export const useAddPost = () => {
  const { showError } = useSnackbar();

  return useMutation({
    mutationFn: submitPost,
    onError: (error: any) => {
      const msg = error?.response?.data?.message || error?.message || 'Something went wrong';
      showError(msg);
    },
  });
};

export const useUpdatePost = () => {
  const { showError } = useSnackbar();

  return useMutation({
    mutationFn: updatePosts,
    onError: (error: any) => {
      const msg = error?.response?.data?.message || error?.message || 'Something went wrong';
      showError(msg);
    },
  });
};

export const useDeletePost = () => {
  const { showError } = useSnackbar();

  return useMutation({
    mutationFn: deletePosts,
    onError: (error: any) => {
      const msg = error?.response?.data?.message || error?.message || 'Something went wrong';
      showError(msg);
    },
  });
};
