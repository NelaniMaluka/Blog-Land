import { useParams } from 'react-router-dom';
import { useGetCategories } from '../hooks/useCategory';
import { PostsLayout } from '../components/ui/postLayout/PostsLayout';
import { Order } from '../types/post/response';

export const CategoryPage = () => {
  const { slug } = useParams<{ slug: string }>();
  const { data: categories } = useGetCategories();

  if (!categories) return <div>Loading categories...</div>;

  const decodedSlug = decodeURIComponent(slug || '');
  const category = categories.find((c) => c.name.toLowerCase() === decodedSlug.toLowerCase());

  if (!category) return <div>Category not found</div>;

  return <PostsLayout title={category.name} categoryId={category.id} showOrderButtons={true} />;
};
