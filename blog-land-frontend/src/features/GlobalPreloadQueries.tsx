import { useGetCategories } from '../hooks/useCategory';
import { useGetUser } from '../hooks/useUser';
import {
  useGetAllPost,
  useGetTopPosts,
  useGetTrendingPosts,
  useGetLatestPosts,
  useGetAllUserPost,
} from '../hooks/usePost';
import { useGetAllUserComments } from '../hooks/useComment';
import { Order } from '../types/post/response';
import { store } from '../store/store';

export function GlobalPreloadQueries() {
  const isAuthenticated = store.getState().auth.isAuthenticated;

  useGetCategories();
  useGetAllPost({ page: 0, size: 10, order: Order.LATEST });
  useGetTopPosts();
  useGetLatestPosts({ page: 1, size: 10 });
  useGetTrendingPosts({ page: 0, size: 10 });

  // Always call, but disable fetching if not authenticated
  useGetUser({ enabled: isAuthenticated });
  useGetAllUserPost({ page: 0, size: 10, options: { enabled: isAuthenticated } });
  useGetAllUserComments({ page: 0, size: 10, options: { enabled: isAuthenticated } });

  return null;
}
