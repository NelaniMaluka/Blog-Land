import { useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { PostsLayout } from '../components/layouts/postLayout/PostsLayout';
import { useGetAllPost } from '../hooks/usePost';
import { Order } from '../types/post/response';
import { Helmet } from 'react-helmet-async';

export const ViewAllPage = () => {
  const [searchParams, setSearchParams] = useSearchParams();

  // read page & order from query params
  const pageParam = parseInt(searchParams.get('page') || '1', 10);
  const orderParam = (searchParams.get('order') as Order) || Order.LATEST;

  // internal state synced with URL
  const [page, setPage] = useState(pageParam - 1); // backend is 0-indexed
  const [order, setOrder] = useState<Order>(orderParam);

  const { data, isLoading, isError } = useGetAllPost({ page, size: 12, order });
  const posts = data?.content ?? [];
  const totalPages = data?.totalPages ?? 0;

  const title = 'All Blog Posts – Blog Land';
  const description =
    'Explore all the latest and most popular blog posts on Blog Land. Read articles, insights, and stories from our community of writers.';
  const canonicalUrl = window.location.href;

  const structuredData = {
    '@context': 'https://schema.org',
    '@type': 'CollectionPage',
    name: title,
    description: description,
    url: canonicalUrl,
  };

  // handlers that sync URL + state
  const handlePageChange = (newPage: number) => {
    setPage(newPage);
    setSearchParams({ page: String(newPage + 1), order }); // keep order
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleOrderChange = (newOrder: Order) => {
    setOrder(newOrder);

    setSearchParams({
      page: String(page + 1),
      order: newOrder,
    });
  };

  return (
    <>
      <Helmet>
        <title>{title}</title>
        <meta name="description" content={description} />
        <meta
          name="keywords"
          content="Blog Land, all posts, latest posts, popular articles, community blogs, storytelling, articles"
        />
        <meta name="author" content="Nelani Maluka" />
        <link rel="canonical" href={canonicalUrl} />

        {/* Open Graph */}
        <meta property="og:title" content={title} />
        <meta property="og:description" content={description} />
        <meta property="og:type" content="website" />
        <meta property="og:url" content={canonicalUrl} />

        {/* Twitter Card */}
        <meta name="twitter:card" content="summary_large_image" />
        <meta name="twitter:title" content={title} />
        <meta name="twitter:description" content={description} />

        {/* Structured Data */}
        <script type="application/ld+json">{JSON.stringify(structuredData)}</script>
      </Helmet>

      <PostsLayout
        title="All Posts"
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
