import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import {
  fetchSearchedPosts,
  fetchRandomPost,
  fetchPost,
  fetchAllPosts,
  fetchTopPosts,
  fetchTrendingPosts,
  fetchPostByCategory,
  fetchAllUserPosts,
  submitView,
} from '../services/postService';
import { useDebounce } from './useDebounce';
import { Order } from '../types/post/response';
import { useMutation } from '@tanstack/react-query';
import { addViewToPost } from '../api/postApi';

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

export const useGetAllPost = (page: number, size: number, order: Order) => {
  return useQuery({
    queryKey: ['allPosts', page, size, order],
    queryFn: () => fetchAllPosts(page, size, order),
  });
};

export const useGetTopPosts = () => {
  return useQuery({
    queryKey: ['topPosts'],
    queryFn: () => fetchTopPosts(),
  });
};

export const useGetTrendingPosts = (page: number, size: number) => {
  return useQuery({
    queryKey: ['trendingPosts', page, size],
    queryFn: () => fetchTrendingPosts(page, size),
  });
};

export const useGetCategoryPosts = (
  categoryId: number,
  page: number,
  size: number,
  order: Order
) => {
  return useQuery({
    queryKey: ['categoryPosts', categoryId, page, size, order],
    queryFn: () => fetchPostByCategory(categoryId, page, size, order),
  });
};

export const useGetAllUserPost = (page: number, size: number) => {
  return useQuery({
    queryKey: ['userPosts', page, size],
    queryFn: () => fetchAllUserPosts(page, size),
  });
};

export const useAddViewCount = () => {
  return useMutation({
    mutationFn: submitView,
  });
};
