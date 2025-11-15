import { useState } from 'react';
import {
  fetchSearchedPosts,
  fetchRandomPost,
  fetchRandomPosts,
  fetchPost,
  fetchAllPosts,
  fetchLatestPosts,
  fetchTrendingPosts,
  submitView,
} from '../services/postService';
import { useDebounce } from './useDebounce';
import { Order } from '../types/post/response';
import { useMutation } from '@tanstack/react-query';
import { useQuery } from '@tanstack/react-query';
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
  return useQuery({ queryKey: ['random-post'], queryFn: () => fetchRandomPost() });
};

export const useGetRandomPosts = () => {
  return useQuery({
    queryKey: ['random-posts'],
    queryFn: () => fetchRandomPosts(),
    refetchOnWindowFocus: true,
  });
};

export const useGetPost = (id: string) => {
  const queryClient = useQueryClient();

  const query = useQuery({
    queryKey: ['single-post', id],
    queryFn: () => fetchPost(id),
    enabled: !!id,
  });

  useWebSocket(`/topic/posts/update/${id}`, (message) => {
    const raw = JSON.parse(message);
    const updatedPost: PostResponse = {
      ...raw,
      title: he.decode(stripHtml(raw.title)),
      summary: raw.summary ? he.decode(stripHtml(raw.summary)) : null,
      createdAt: formatDate(raw.createdAt),
    };

    queryClient.setQueryData(['singlePost', id], () => {
      return updatedPost;
    });
  });

  return query;
};

export const useGetAllPost = (payload: { page: number; size: number; order: Order }) => {
  return useQuery({
    queryKey: ['all-posts', payload],
    queryFn: () => fetchAllPosts(payload),
  });
};

export const useGetLatestPosts = ({
  page,
  size,
  enabled = true,
}: {
  page: number;
  size: number;
  enabled?: boolean;
}) =>
  useQuery({
    queryKey: ['latest-posts', page],
    queryFn: () => fetchLatestPosts({ page, size }),
    enabled,
  });

export const useGetTrendingPosts = (payload: { page: number; size: number }) => {
  return useQuery({
    queryKey: ['trending-posts', payload],
    queryFn: () => fetchTrendingPosts(payload),
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
