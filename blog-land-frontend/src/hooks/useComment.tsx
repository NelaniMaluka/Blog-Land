import { useQuery } from '@tanstack/react-query';
import { fetchAllUserComments } from '../services/commentService';

export const useGetAllUserComments = (page: number, size: number) => {
  return useQuery({
    queryKey: ['userComments', page, size],
    queryFn: () => fetchAllUserComments(page, size),
  });
};
