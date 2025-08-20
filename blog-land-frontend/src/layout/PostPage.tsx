import { useParams } from 'react-router-dom';
import { SinglePostLayout } from '../components/layouts/singlePostLayout/SinglePostLayout';
import { useGetPost } from '../hooks/usePost';
import { Helmet } from 'react-helmet-async';

export const PostPage = () => {
  const { slug } = useParams<{ slug?: string }>();
  const id = slug ? Number(slug) : 0;

  const { data: post, isLoading, isError } = useGetPost({ id });

  const canonicalUrl = window.location.href;

  const title = post?.title || 'Blog Post – Blog Land';
  const description =
    post?.summary ||
    'Read this insightful post on Blog Land, a community platform for writers and readers.';
  const imageUrl = post?.postImgUrl || `${window.location.origin}/og-image-default.jpg`;
  const keywords = 'Blog Land, blog post, article, writers, readers';

  const structuredData = post
    ? {
        '@context': 'https://schema.org',
        '@type': 'BlogPosting',
        headline: title,
        image: [imageUrl],
        author: {
          '@type': 'Person',
          name: post.author || 'Blog Land',
        },
        publisher: {
          '@type': 'Organization',
          name: 'Blog Land',
          logo: {
            '@type': 'ImageObject',
            url: `${window.location.origin}/logo.png`,
          },
        },
        datePublished: post.createdAt,
        dateModified: post.updatedAt || post.updatedAt,
        mainEntityOfPage: {
          '@type': 'WebPage',
          '@id': canonicalUrl,
        },
        description: description,
      }
    : null;

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
        {structuredData && (
          <script type="application/ld+json">{JSON.stringify(structuredData)}</script>
        )}
      </Helmet>

      <SinglePostLayout post={post} isLoading={isLoading} isError={isError} />
    </>
  );
};
