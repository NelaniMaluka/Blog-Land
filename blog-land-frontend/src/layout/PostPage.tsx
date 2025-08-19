import { useParams } from 'react-router-dom';
import { SinglePostLayout } from '../components/layouts/singlePostLayout/SinglePostLayout';
import { useGetPost } from '../hooks/usePost';

export const PostPage = () => {
  const { slug } = useParams<{ slug?: string }>();
  const id = slug ? Number(slug) : 0;

  const { data, isLoading, isError } = useGetPost({ id });

  return <SinglePostLayout post={data} isLoading={isLoading} isError={isError} />;
};
