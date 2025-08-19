import { useGetRandomPost } from '../hooks/usePost';
import { SinglePostLayout } from '../components/layouts/singlePostLayout/SinglePostLayout';

export const RandomPostPage = () => {
  const { data, isLoading, isError } = useGetRandomPost();
  console.log(data);

  return <SinglePostLayout post={data} isLoading={isLoading} isError={isError} />;
};
