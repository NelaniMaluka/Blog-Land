import { useGetLatestPosts } from '../hooks/usePost';
import { useParams } from 'react-router-dom';
import { SinglePostLayout } from '../components/layouts/singlePostLayout/SinglePostLayout';
import { Helmet } from 'react-helmet-async';

export const LatestPostPage = () => {
  const { slug } = useParams<{ slug?: string }>();
  const { data: latestPosts, isLoading, isError } = useGetLatestPosts({ page: 1, size: 20 });

  const post = latestPosts?.find((post) => post.title === slug);

  const canonicalUrl = window.location.href;

  // Fallback SEO values
  const title = post?.title || 'Latest Post – Blog Land';
  const description =
    post?.summary ||
    'Read the latest post on Blog Land, the community platform for writers and readers.';
  const imageUrl = post?.postImgUrl || `${window.location.origin}/og-image-latest.jpg`;
  const keywords = 'Blog Land, latest posts, articles, blog, writers, readers';

  const structuredData = {
    '@context': 'https://schema.org',
    '@type': 'BlogPosting',
    headline: title,
    image: [imageUrl],
    author: {
      '@type': 'Person',
      name: post?.author || 'Blog Land',
    },
    publisher: {
      '@type': 'Organization',
      name: 'Blog Land',
      logo: {
        '@type': 'ImageObject',
        url: `${window.location.origin}/logo.png`,
      },
    },
    datePublished: post?.createdAt,
    dateModified: post?.updatedAt || post?.updatedAt,
    mainEntityOfPage: {
      '@type': 'WebPage',
      '@id': canonicalUrl,
    },
    description: description,
  };

  return (
    <>
      <Helmet>
        <title>{title} – Blog Land</title>
        <meta name="description" content={description} />
        <meta name="keywords" content={keywords} />
        <meta name="author" content={post?.author || 'Nelani Maluka'} />
        <link rel="canonical" href={canonicalUrl} />

        {/* Open Graph */}
        <meta property="og:title" content={title} />
        <meta property="og:description" content={description} />
        <meta property="og:type" content="article" />
        <meta property="og:url" content={canonicalUrl} />
        <meta property="og:image" content={imageUrl} />

        {/* Twitter Card */}
        <meta name="twitter:card" content="summary_large_image" />
        <meta name="twitter:title" content={title} />
        <meta name="twitter:description" content={description} />
        <meta name="twitter:image" content={imageUrl} />

        {/* Structured Data */}
        <script type="application/ld+json">{JSON.stringify(structuredData)}</script>
      </Helmet>

      <SinglePostLayout post={post} isLoading={isLoading} isError={isError} />
    </>
  );
};
