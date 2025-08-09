import { useQuery } from '@tanstack/react-query';
import { fetchCategories } from '../services/categoryService';

export const useGetCategories = () => {
  return useQuery({ queryKey: ['categories'], queryFn: fetchCategories });
};
