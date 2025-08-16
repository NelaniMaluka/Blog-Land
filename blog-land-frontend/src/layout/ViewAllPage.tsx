import { useState } from 'react';
import { PostsLayout } from '../components/ui/postLayout/PostsLayout';
import { useGetAllPost } from '../hooks/usePost';
import { Order } from '../types/post/response';

export const ViewAllPage = () => {
  const [order, setOrder] = useState<Order>(Order.OLDEST);
  const [page, setPage] = useState(0);

  const { data, isLoading, isError } = useGetAllPost({ page, size: 12, order });
  const posts = data?.content ?? [];
  const totalPages = data?.totalPages ?? 0;

  return (
    <PostsLayout
      title="All Posts"
      posts={posts}
      isLoading={isLoading}
      isError={isError}
      page={page}
      setPage={setPage}
      totalPages={totalPages}
      order={order}
      setOrder={setOrder}
      showOrderButtons={true}
      totalElements={data?.totalElements || 0}
    />
  );
};
