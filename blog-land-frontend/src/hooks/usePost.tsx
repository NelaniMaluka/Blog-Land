import { useState } from 'react';
import {
  fetchSearchedPosts,
  fetchRandomPost,
  fetchRandomPosts,
  fetchPost,
  fetchAllPosts,
  fetchTopPosts,
  fetchLatestPosts,
  fetchTrendingPosts,
  fetchPostByCategory,
  fetchAllUserPosts,
  submitView,
  submitPost,
  updatePosts,
  deletePosts,
} from '../services/postService';
import { useDebounce } from './useDebounce';
import { Order } from '../types/post/response';
import { useMutation } from '@tanstack/react-query';
import { useQuery, UseQueryResult } from '@tanstack/react-query';
import { PaginatedPosts } from '../types/post/response';
import { useSnackbar } from '../features/Snackbars/errorMessage';
import { PostResponse } from '../types/post/response';
import { useEffect } from 'react';
import { useWebSocket } from './useWebSocket';
import { useQueryClient } from '@tanstack/react-query';
import { formatDate } from '../utils/formatUtils';
import { stripHtml } from '../utils/formatUtils';
import he from 'he';

export const useSearchPost = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const debouncedTerm = useDebounce(searchTerm, 300);
  const { showError } = useSnackbar();

  const { data, isLoading, isError, error } = useQuery<PostResponse[], Error>({
    queryKey: ['search', debouncedTerm],
    queryFn: () => fetchSearchedPosts(debouncedTerm),
    enabled: !!debouncedTerm,
  });

  useEffect(() => {
    if (isError && error) {
      const msg =
        (error as any)?.response?.data?.message || error.message || 'Failed to fetch posts';
      showError(msg);
    }
  }, [isError, error, showError]);

  return {
    searchTerm,
    setSearchTerm,
    results: data,
    isLoading,
    isError,
    error,
  };
};

export const useGetRandomPost = () => {
  return useQuery({ queryKey: ['randomPost'], queryFn: () => fetchRandomPost() });
};

export const useGetRandomPosts = () => {
  return useQuery({
    queryKey: ['randomPosts'],
    queryFn: () => fetchRandomPosts(),
    refetchOnWindowFocus: true,
  });
};

export const useGetPost = (payload: { id: number }) => {
  const queryClient = useQueryClient();

  const query = useQuery({
    queryKey: ['singlePost', payload],
    queryFn: () => fetchPost(payload),
    enabled: !!payload,
  });

  useWebSocket(`/topic/post/update/${payload.id}`, (message) => {
    const raw = JSON.parse(message);
    const updatedPost: PostResponse = {
      ...raw,
      title: he.decode(stripHtml(raw.title)),
      summary: raw.summary ? he.decode(stripHtml(raw.summary)) : null,
      createdAt: formatDate(raw.createdAt),
    };

    queryClient.setQueryData(['singlePost', payload], () => {
      return updatedPost;
    });
  });

  return query;
};

export const useGetAllPost = (payload: { page: number; size: number; order: Order }) => {
  return useQuery({
    queryKey: ['allPosts', payload],
    queryFn: () => fetchAllPosts(payload),
  });
};

export const useGetTopPosts = () => {
  return useQuery({
    queryKey: ['topPosts'],
    queryFn: () => fetchTopPosts(),
  });
};

export const useGetLatestPosts = (payload: { page: number; size: number }) => {
  return useQuery({
    queryKey: ['latestPosts', payload],
    queryFn: () => fetchLatestPosts(payload),
  });
};

export const useGetTrendingPosts = (payload: { page: number; size: number }) => {
  return useQuery({
    queryKey: ['trendingPosts', payload],
    queryFn: () => fetchTrendingPosts(payload),
  });
};

export const useGetCategoryPosts = (payload: {
  categoryId: number;
  page: number;
  size: number;
  order: Order;
}): UseQueryResult<PaginatedPosts, Error> => {
  return useQuery<PaginatedPosts, Error>({
    queryKey: ['categoryPosts', payload.categoryId, payload.page, payload.size, payload.order],
    queryFn: () => fetchPostByCategory(payload),
  });
};

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

export const useAddViewCount = () => {
  const { showError } = useSnackbar();

  return useMutation({
    mutationFn: submitView,
    onError: (error: any) => {
      const msg = error?.response?.data?.message || error?.message || 'Something went wrong';
      showError(msg);
    },
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
