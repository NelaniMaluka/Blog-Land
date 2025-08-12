import { useQuery } from '@tanstack/react-query';
import { fetchArticle } from '../services/techCrunchService';

export const useGetArticles = () => {
  return useQuery({
    queryKey: ['articles'],
    queryFn: () => fetchArticle(),
  });
};
