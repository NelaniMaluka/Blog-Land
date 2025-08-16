import { useState } from 'react';
import { useParams } from 'react-router-dom';
import { useGetCategories } from '../hooks/useCategory';
import { useGetCategoryPosts } from '../hooks/usePost';
import { PostsLayout } from '../components/ui/postLayout/PostsLayout';
import { Order } from '../types/post/response';
import { PostResponse } from '../types/post/response';

export const CategoryPage = () => {
  const { slug } = useParams<{ slug: string }>();
  const { data: categories } = useGetCategories();
  const [page, setPage] = useState(0);
  const [order, setOrder] = useState<Order>(Order.LATEST);

  if (!categories) return <div>Loading categories...</div>;

  const decodedSlug = decodeURIComponent(slug || '');
  const category = categories.find((c) => c.name.toLowerCase() === decodedSlug.toLowerCase());

  if (!category) return <div>Category not found</div>;

  // Call the hook directly with page & order
  const { data, isLoading, isError } = useGetCategoryPosts({
    categoryId: category.id,
    page,
    size: 12,
    order,
  });

  const posts: PostResponse[] = data?.content ?? [];
  const totalPages: number = data?.totalPages ?? 0;

  return (
    <PostsLayout
      title={category.name}
      posts={posts}
      isLoading={isLoading}
      isError={isError}
      page={page}
      setPage={setPage}
      totalPages={totalPages}
      order={order}
      setOrder={setOrder}
      showOrderButtons={true}
    />
  );
};
