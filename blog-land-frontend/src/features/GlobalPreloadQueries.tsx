import { useGetUser } from '../hooks/useUser';
import { useGetAllPost, useGetTrendingPosts, useGetLatestPosts } from '../hooks/usePost';
import { Order } from '../types/post/response';
import { store } from '../store/store';
import { useGetAllUserPost } from '../hooks/useUserPost';

export function GlobalPreloadQueries() {
  const isAuthenticated = store.getState().auth.isAuthenticated;

  useGetAllPost({ page: 0, size: 10, order: Order.LATEST });
  useGetLatestPosts({ page: 1, size: 20 });
  useGetLatestPosts({ page: 2, size: 20 });
  useGetLatestPosts({ page: 3, size: 20 });
  useGetTrendingPosts({ page: 0, size: 12 });

  // Always call, but disable fetching if not authenticated
  useGetUser();
  useGetAllUserPost({ page: 0, size: 10, options: { enabled: isAuthenticated } });

  return null;
}
