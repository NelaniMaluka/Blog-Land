import { useState } from 'react';
import { useParams } from 'react-router-dom';
import { useGetCategories } from '../hooks/useCategory';
import { useGetCategoryPosts } from '../hooks/usePost';
import { PostsLayout } from '../components/layouts/postLayout/PostsLayout';
import { Order } from '../types/post/response';
import { PostResponse } from '../types/post/response';

export const CategoryPage = () => {
  const { slug } = useParams<{ slug: string }>();
  const { data: categories } = useGetCategories();
  const [page, setPage] = useState(0);
  const [order, setOrder] = useState<Order>(Order.LATEST);

  const decodedSlug = decodeURIComponent(slug || '');
  const category = categories?.find((c) => c.name.toLowerCase() === decodedSlug.toLowerCase());

  // Always call the hook, even if category is undefined
  const { data, isLoading, isError } = useGetCategoryPosts({
    categoryId: category?.id || 0,
    page,
    size: 12,
    order,
  });

  const posts: PostResponse[] = data?.content ?? [];
  const totalPages: number = data?.totalPages ?? 0;

  return (
    <PostsLayout
      title={category?.name || 'Category'}
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
