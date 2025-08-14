import { useParams } from 'react-router-dom';
import { useGetCategories } from '../hooks/useCategory';

export const CategoryPage = () => {
  const { slug } = useParams<{ slug: string }>();
  const { data: categories } = useGetCategories();

  // decode in case there are spaces
  const decodedSlug = decodeURIComponent(slug || '');
  const category = categories?.find((c) => c.name.toLowerCase() === decodedSlug.toLowerCase());

  return <div>Showing posts for category: {category?.name}</div>;
};
