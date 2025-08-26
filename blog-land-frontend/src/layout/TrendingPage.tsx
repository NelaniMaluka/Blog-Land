import { useState } from 'react';
import { PostsLayout } from '../components/layouts/postLayout/PostsLayout';
import { useGetTrendingPosts } from '../hooks/usePost';
import { Order } from '../types/post/response';
import { Helmet } from 'react-helmet-async';
import { useSearchParams } from 'react-router-dom';

export const TrendingPage = () => {
  const [searchParams, setSearchParams] = useSearchParams();
  const pageParam = parseInt(searchParams.get('page') || '1', 10);
  const [page, setPage] = useState(pageParam - 1);

  const { data, isLoading, isError } = useGetTrendingPosts({ page, size: 12 });
  const totalPages = data?.totalPages ?? 0;

  const handlePageChange = (newPage: number) => {
    setPage(newPage);
    setSearchParams({ page: String(newPage + 1) }); // URL gets updated
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const title = 'Trending Posts – Blog Land';
  const description =
    'Discover the most popular and trending posts on Blog Land. Stay updated with top stories, engaging articles, and community favorites from our blogging platform.';
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
          content="Blog Land, trending posts, popular articles, top stories, blogging platform, trending blogs"
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
        title="Trending"
        posts={data?.content ?? []}
        isLoading={isLoading}
        isError={isError}
        page={page}
        setPage={handlePageChange}
        totalPages={totalPages}
        order={Order.LATEST}
        setOrder={() => {}}
        showOrderButtons={false}
        totalElements={data?.totalElements || 0}
      />
    </>
  );
};
