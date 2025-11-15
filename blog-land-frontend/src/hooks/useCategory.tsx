import { useQuery, UseQueryResult } from '@tanstack/react-query';
import { fetchCategories, fetchPostByCategory } from '../services/categoryService';
import { Order, PaginatedPosts } from '../types/post/response';

export const useGetCategories = () => {
  return useQuery({ queryKey: ['categories'], queryFn: fetchCategories });
};

export const useGetCategoryPosts = (
  categoryId: string,
  page: {
    page: number;
    size: number;
    order: Order;
  }
): UseQueryResult<PaginatedPosts, Error> => {
  return useQuery<PaginatedPosts, Error>({
    queryKey: ['categoryPosts', categoryId, page.page, page.size, page.order],
    queryFn: () => fetchPostByCategory({ categoryId, page }),
  });
};
