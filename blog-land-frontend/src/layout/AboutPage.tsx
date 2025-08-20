import { Helmet } from 'react-helmet-async';
import styles from '../components/common/LegalPages.module.css';

export const AboutPage = () => {
  const canonicalUrl = window.location.href;

  const structuredData = {
    '@context': 'https://schema.org',
    '@type': 'AboutPage',
    name: 'About Blog Land',
    url: canonicalUrl,
    description:
      'Learn about Blog Land, the community-driven platform where writers, readers, and creators come together.',
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
    <div className="container">
      <Helmet>
        <title>About Blog Land – A Community for Bloggers & Readers</title>
        <meta
          name="description"
          content="Learn about Blog Land, the community-driven platform where writers, readers, and creators come together. Share your story and connect with like-minded people."
        />
        <meta
          name="keywords"
          content="Blog Land, blogging platform, writers, readers, storytelling, community blog"
        />
        <meta name="author" content="Nelani Maluka" />

        {/* Canonical */}
        <link rel="canonical" href={canonicalUrl} />

        {/* Open Graph */}
        <meta property="og:title" content="About Blog Land – A Community for Bloggers & Readers" />
        <meta
          property="og:description"
          content="Blog Land is a platform where every voice matters. Join our community of writers and readers today!"
        />
        <meta property="og:type" content="website" />
        <meta property="og:url" content={canonicalUrl} />
        <meta property="og:image" content={`${window.location.origin}/og-image-about.jpg`} />

        {/* Twitter Card */}
        <meta name="twitter:card" content="summary_large_image" />
        <meta name="twitter:title" content="About Blog Land – A Community for Bloggers & Readers" />
        <meta
          name="twitter:description"
          content="Blog Land is a platform where every voice matters. Join our community of writers and readers today!"
        />
        <meta name="twitter:image" content={`${window.location.origin}/og-image-about.jpg`} />

        {/* Structured Data */}
        <script type="application/ld+json">{JSON.stringify(structuredData)}</script>
      </Helmet>

      <div className={styles.LegalPages}>
        <h1 className={styles.title}>About Blog Land</h1>
        <p className={styles.text}>
          Welcome to <strong>Blog Land</strong>, your digital space to share ideas, stories, and
          insights. Our platform is designed to bring writers, readers, and creators together into
          one community-driven hub where everyone has a voice.
        </p>

        <p className={styles.text}>
          Whether you’re an aspiring blogger or an experienced writer, Blog Land provides the tools
          and audience to grow your passion for storytelling. Our mission is to make publishing
          simple, engaging, and rewarding.
        </p>

        <h2 className={styles.subtitle}>Our Mission</h2>
        <p className={styles.text}>
          At Blog Land, we believe that every story matters. We aim to empower individuals to
          express themselves and connect with others through the written word. From casual posts to
          professional insights, every blog contributes to a larger conversation.
        </p>

        <h2 className={styles.subtitle}>Why Blog Land?</h2>
        <ul className={styles.list}>
          <li>Easy-to-use publishing tools</li>
          <li>A growing community of readers & writers</li>
          <li>Personalization to fit your writing style</li>
          <li>Commitment to privacy and content security</li>
        </ul>

        <p className={styles.text}>
          Thank you for being a part of Blog Land. Together, let’s build a platform where creativity
          thrives and every voice can be heard.
        </p>
      </div>
    </div>
  );
};
