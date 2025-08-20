import { Helmet } from 'react-helmet-async';
import ShuffleHero from '../components/ui/HomePage/hero/LatestCta';
import { Article } from '../components/ui/HomePage/article/Article';
import { LatestSection } from '../components/ui/HomePage/latest/LatestSection';
import { TrendingSection } from '../components/ui/HomePage/trending/Trending';
import QandASection from '../components/ui/HomePage/QandA/QandA';

function HomePage() {
  const canonicalUrl = window.location.href;

  // Example keywords - you can also generate dynamically from latest posts
  const keywords =
    'Blog Land, blogging platform, latest articles, trending posts, Q&A, community blog, writers, readers';

  const structuredData = {
    '@context': 'https://schema.org',
    '@type': 'WebPage',
    name: 'Blog Land – Home',
    url: canonicalUrl,
    description:
      'Blog Land is a community-driven platform where writers, readers, and creators connect. Discover latest posts, trending articles, and Q&A insights.',
    publisher: {
      '@type': 'Organization',
      name: 'Blog Land',
      logo: {
        '@type': 'ImageObject',
        url: `${window.location.origin}/logo.png`,
      },
    },
    mainEntity: [
      {
        '@type': 'CollectionPage',
        name: 'Latest Posts',
        url: `${canonicalUrl}#latest`,
      },
      {
        '@type': 'CollectionPage',
        name: 'Trending Posts',
        url: `${canonicalUrl}#trending`,
      },
      {
        '@type': 'CollectionPage',
        name: 'Articles',
        url: `${canonicalUrl}#articles`,
      },
      {
        '@type': 'CollectionPage',
        name: 'Q&A',
        url: `${canonicalUrl}#qanda`,
      },
    ],
  };

  return (
    <>
      <Helmet>
        <title>Blog Land – Latest Posts, Trending Articles & Community Blog</title>
        <meta
          name="description"
          content="Explore Blog Land, the community platform for writers, readers, and creators. Discover latest posts, trending articles, and Q&A insights."
        />
        <meta name="keywords" content={keywords} />
        <meta name="author" content="Nelani Maluka" />
        <link rel="canonical" href={canonicalUrl} />

        {/* Open Graph */}
        <meta
          property="og:title"
          content="Blog Land – Latest Posts, Trending Articles & Community Blog"
        />
        <meta
          property="og:description"
          content="Discover latest posts, trending articles, and Q&A insights on Blog Land – a community for writers and readers."
        />
        <meta property="og:type" content="website" />
        <meta property="og:url" content={canonicalUrl} />
        <meta property="og:image" content={`${window.location.origin}/og-image-home.jpg`} />

        {/* Twitter Card */}
        <meta name="twitter:card" content="summary_large_image" />
        <meta
          name="twitter:title"
          content="Blog Land – Latest Posts, Trending Articles & Community Blog"
        />
        <meta
          name="twitter:description"
          content="Explore Blog Land, the community platform for writers, readers, and creators. Discover latest posts, trending articles, and Q&A insights."
        />
        <meta name="twitter:image" content={`${window.location.origin}/og-image-home.jpg`} />

        {/* Structured Data */}
        <script type="application/ld+json">{JSON.stringify(structuredData)}</script>
      </Helmet>

      <LatestSection />
      <ShuffleHero />
      <TrendingSection />
      <Article />
      <QandASection />
    </>
  );
}

export default HomePage;
