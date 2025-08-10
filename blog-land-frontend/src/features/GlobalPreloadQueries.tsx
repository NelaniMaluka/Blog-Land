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
  // Runs these on page load
  useGetCategories();

  useGetAllPost({ page: 0, size: 10, order: Order.LATEST });
  useGetTopPosts();
  useGetLatestPosts({ page: 1, size: 10 });
  useGetTrendingPosts({ page: 0, size: 10 });

  if (store.getState().auth.isAuthenticated) {
    useGetUser();
    useGetAllUserPost({ page: 0, size: 10 });
    useGetAllUserComments({ page: 0, size: 10 });
  }

  return null;
}
