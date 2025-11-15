import { useState } from 'react';
import { useParams, useSearchParams } from 'react-router-dom';
import { useGetCategories } from '../hooks/useCategory';
import { useGetCategoryPosts } from '../hooks/useCategory';
import { PostsLayout } from '../components/layouts/postLayout/PostsLayout';
import { Order, PostResponse } from '../types/post/response';
import { Helmet } from 'react-helmet-async';

export const CategoryPage = () => {
  const { slug } = useParams<{ slug: string }>();
  const { data: categories, isLoading: isCategoriesLoading } = useGetCategories();
  const [searchParams, setSearchParams] = useSearchParams();

  // read from URL
  const pageParam = parseInt(searchParams.get('page') || '1', 10);
  const orderParam = (searchParams.get('order') as Order) || Order.LATEST;

  // internal state synced with URL
  const [page, setPage] = useState(pageParam - 1); // backend 0-indexed
  const [order, setOrder] = useState<Order>(orderParam);

  const decodedSlug = decodeURIComponent(slug || '');
  const category = categories?.find((c) => c.name.toLowerCase() === decodedSlug.toLowerCase());

  const { data, isLoading, isError } = useGetCategoryPosts(category?.id ?? '', {
    page,
    size: 12,
    order,
  });

  const posts: PostResponse[] = data?.content ?? [];
  const totalPages: number = data?.totalPages ?? 0;
  const totalElements: number = data?.totalElements || 0;
  const canonicalUrl = window.location.href;

  // --- handlers that sync URL + state ---
  const handlePageChange = (newPage: number) => {
    setPage(newPage);
    setSearchParams({ page: String(newPage + 1), order });
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleOrderChange = (newOrder: Order) => {
    setOrder(newOrder);
    setSearchParams({ page: String(page + 1), order: newOrder });
  };

  // Generate comma-separated keywords from posts
  const keywords = posts.map((p) => p.title).join(', ');

  const structuredData = {
    '@context': 'https://schema.org',
    '@type': 'CollectionPage',
    name: category?.name || 'Category',
    url: canonicalUrl,
    description: `Explore posts in the ${category?.name || 'category'} on Blog Land.`,
    hasPart: posts.map((p) => ({
      '@type': 'BlogPosting',
      headline: p.title,
      url: `${window.location.origin}/post/${p.id}`,
      author: { '@type': 'Person', name: p.author },
      datePublished: p.createdAt,
      image: p.postImgUrl || `${window.location.origin}/default-post.jpg`,
    })),
  };

  return (
    <>
      <Helmet>
        <title>{category?.name ? `${category.name} – Blog Land` : 'Category – Blog Land'}</title>
        <meta
          name="description"
          content={`Read the latest posts in the ${category?.name || 'category'} on Blog Land.`}
        />
        <meta
          name="keywords"
          content={`Blog Land, ${category?.name}, blogging, posts, ${keywords}`}
        />
        <meta name="author" content="Nelani Maluka" />
        <link rel="canonical" href={canonicalUrl} />

        {/* Open Graph */}
        <meta property="og:title" content={category?.name || 'Category – Blog Land'} />
        <meta
          property="og:description"
          content={`Explore posts in the ${category?.name || 'category'} on Blog Land.`}
        />
        <meta property="og:type" content="website" />
        <meta property="og:url" content={canonicalUrl} />

        {/* Twitter Card */}
        <meta name="twitter:card" content="summary_large_image" />
        <meta name="twitter:title" content={category?.name || 'Category – Blog Land'} />
        <meta
          name="twitter:description"
          content={`Explore posts in the ${category?.name || 'category'} on Blog Land.`}
        />

        {/* Structured Data */}
        <script type="application/ld+json">{JSON.stringify(structuredData)}</script>
      </Helmet>

      <PostsLayout
        title={category?.name}
        posts={posts}
        isLoading={isLoading}
        isError={isError}
        page={page}
        setPage={handlePageChange}
        totalPages={totalPages}
        order={order}
        setOrder={handleOrderChange}
        showOrderButtons={true}
        totalElements={data?.totalElements || 0}
      />
    </>
  );
};
