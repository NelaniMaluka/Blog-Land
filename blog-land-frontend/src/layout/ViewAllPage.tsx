import { useState } from 'react';
import { PostsLayout } from '../components/layouts/postLayout/PostsLayout';
import { useGetAllPost } from '../hooks/usePost';
import { Order } from '../types/post/response';
import { Helmet } from 'react-helmet-async';

export const ViewAllPage = () => {
  const [order, setOrder] = useState<Order>(Order.LATEST);
  const [page, setPage] = useState(0);

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
        setPage={setPage}
        totalPages={totalPages}
        order={order}
        setOrder={setOrder}
        showOrderButtons={true}
        totalElements={data?.totalElements || 0}
      />
    </>
  );
};
