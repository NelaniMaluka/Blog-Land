import { useGetLatestPosts } from '../hooks/usePost';
import { useParams } from 'react-router-dom';
import { SinglePostLayout } from '../components/layouts/singlePostLayout/SinglePostLayout';

export const LatestPostPage = () => {
  const { slug } = useParams<{ slug?: string }>();
  const { data: latestPosts, isLoading, isError } = useGetLatestPosts({ page: 1, size: 20 });

  const post = latestPosts?.find((post) => post.title === slug);

  return <SinglePostLayout post={post} isLoading={isLoading} isError={isError} />;
};
