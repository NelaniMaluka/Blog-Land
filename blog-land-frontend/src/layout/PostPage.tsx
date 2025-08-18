import { useParams } from 'react-router-dom';
import { SinglePostLayout } from '../components/ui/singlePostLayout/singlePostLayout';

export const PostPage = () => {
  const { slug } = useParams<{ slug?: string }>();
  const id = slug ? Number(slug) : 0;

  return (
    <>
      <SinglePostLayout id={id} />
    </>
  );
};
