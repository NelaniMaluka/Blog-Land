import { useQuery } from '@tanstack/react-query';
import { fetchArticle, fetchYoutubeVideos } from '../services/techCrunchService';

export const useGetArticles = () => {
  return useQuery({
    queryKey: ['articles'],
    queryFn: () => fetchArticle(),
  });
};

export const useGetYoutubeVideos = () => {
  return useQuery({
    queryKey: ['videos'],
    queryFn: () => fetchYoutubeVideos(),
  });
};
