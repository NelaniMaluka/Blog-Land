import { useGetAllPost } from '../hooks/usePost';
import { PostsLayout } from '../components/ui/postLayout/PostsLayout';
import { Order } from '../types/post/response';

export const ViewAllPage = () => {
  return (
    <PostsLayout
      title="All Posts"
      fetchFn={(params) =>
        useGetAllPost({ page: 0, size: 12, order: params?.order ?? Order.OLDEST })
      }
    />
  );
};
