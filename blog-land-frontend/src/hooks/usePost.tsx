import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import {
  fetchSearchedPosts,
  fetchRandomPost,
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

export const useSearchPost = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const debouncedTerm = useDebounce(searchTerm, 300);

  const { data, isLoading, isError, error } = useQuery({
    queryKey: ['search', debouncedTerm],
    queryFn: () => fetchSearchedPosts(debouncedTerm),
    enabled: !!debouncedTerm,
  });

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

export const useGetPost = (id: number) => {
  return useQuery({
    queryKey: ['singlePost', id],
    queryFn: () => fetchPost(id),
    enabled: !!id,
  });
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
}) => {
  return useQuery({
    queryKey: ['categoryPosts', payload],
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
    queryFn: () => fetchAllUserPosts({ page, size }), // only pass page & size
    enabled: options?.enabled ?? true, // default true
  });
};

export const useAddViewCount = () => {
  return useMutation({
    mutationFn: submitView,
  });
};

export const useAddPost = () => {
  return useMutation({
    mutationFn: submitPost,
  });
};

export const useUpdatePost = () => {
  return useMutation({
    mutationFn: updatePosts,
  });
};

export const useDeletePost = () => {
  return useMutation({
    mutationFn: deletePosts,
  });
};
