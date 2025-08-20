import { LatestLayout } from '../components/layouts/latestLayout/LatestLayout';
import { Helmet } from 'react-helmet-async';

export const LatestPage = () => {
  const canonicalUrl = window.location.href;

  // Example keywords for latest posts
  const keywords =
    'Blog Land, latest posts, new articles, trending blogs, writers, readers, community blog';

  const structuredData = {
    '@context': 'https://schema.org',
    '@type': 'CollectionPage',
    name: 'Latest Posts – Blog Land',
    url: canonicalUrl,
    description:
      'Explore the latest posts on Blog Land, a community-driven platform for writers and readers.',
    publisher: {
      '@type': 'Organization',
      name: 'Blog Land',
      logo: {
        '@type': 'ImageObject',
        url: `${window.location.origin}/logo.png`,
      },
    },
  };

  return (
    <>
      <Helmet>
        <title>Latest Posts – Blog Land</title>
        <meta
          name="description"
          content="Discover the latest posts on Blog Land, where writers and readers connect and share stories."
        />
        <meta name="keywords" content={keywords} />
        <meta name="author" content="Nelani Maluka" />
        <link rel="canonical" href={canonicalUrl} />

        {/* Open Graph */}
        <meta property="og:title" content="Latest Posts – Blog Land" />
        <meta
          property="og:description"
          content="Check out the newest posts and articles on Blog Land, the community platform for writers and readers."
        />
        <meta property="og:type" content="website" />
        <meta property="og:url" content={canonicalUrl} />
        <meta property="og:image" content={`${window.location.origin}/og-image-latest.jpg`} />

        {/* Twitter Card */}
        <meta name="twitter:card" content="summary_large_image" />
        <meta name="twitter:title" content="Latest Posts – Blog Land" />
        <meta
          name="twitter:description"
          content="Discover the latest posts on Blog Land, where writers and readers connect and share stories."
        />
        <meta name="twitter:image" content={`${window.location.origin}/og-image-latest.jpg`} />

        {/* Structured Data */}
        <script type="application/ld+json">{JSON.stringify(structuredData)}</script>
      </Helmet>

      <LatestLayout />
    </>
  );
};
