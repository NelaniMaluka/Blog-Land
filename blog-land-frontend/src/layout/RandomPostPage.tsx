import { useGetRandomPost } from '../hooks/usePost';
import { SinglePostLayout } from '../components/layouts/singlePostLayout/SinglePostLayout';
import { Helmet } from 'react-helmet-async';

export const RandomPostPage = () => {
  const { data, isLoading, isError } = useGetRandomPost();
  const post = data;

  // Fallback values if no post is loaded yet
  const title = post?.title || 'Random Blog Post – Blog Land';
  const description =
    post?.summary ||
    'Discover a random blog post from Blog Land, the community-driven platform for writers and readers.';
  const canonicalUrl = window.location.href;
  const imageUrl = post?.postImgUrl || `${window.location.origin}/og-image-default.jpg`;

  const structuredData = {
    '@context': 'https://schema.org',
    '@type': 'BlogPosting',
    headline: title,
    description: description,
    author: {
      '@type': 'Person',
      name: post?.author || 'Blog Land',
    },
    datePublished: post?.createdAt || new Date().toISOString(),
    url: canonicalUrl,
    image: imageUrl,
  };

  return (
    <>
      <Helmet>
        <title>{title}</title>
        <meta name="description" content={description} />
        <meta
          name="keywords"
          content="Blog Land, random blog post, community blogging, stories, articles, trending blog posts"
        />
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
