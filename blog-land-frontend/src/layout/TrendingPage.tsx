import { useGetTrendingPosts } from '../hooks/usePost';
import { PostsLayout } from '../components/ui/postLayout/PostsLayout';

export const TrendingPage = () => {
  return (
    <PostsLayout
      title="Trending"
      fetchFn={() => useGetTrendingPosts({ page: 0, size: 12 })}
      showOrderButtons={false}
    />
  );
};
