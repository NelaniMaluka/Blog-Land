import { useState } from 'react';
import { PostsLayout } from '../components/ui/postLayout/PostsLayout';
import { useGetTrendingPosts } from '../hooks/usePost';
import { Order } from '../types/post/response';

export const TrendingPage = () => {
  const [page, setPage] = useState(0);
  const { data, isLoading, isError } = useGetTrendingPosts({ page, size: 12 });
  const posts = data?.content ?? [];
  const totalPages = data?.totalPages ?? 0;

  return (
    <PostsLayout
      title="Trending"
      posts={posts}
      isLoading={isLoading}
      isError={isError}
      page={page}
      setPage={setPage}
      totalPages={totalPages}
      order={Order.LATEST}
      setOrder={() => {}}
      showOrderButtons={false}
      totalElements={data?.totalElements || 0}
    />
  );
};
