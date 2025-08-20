import styles from '../components/common/LegalPages.module.css';
import { Helmet } from 'react-helmet-async';

export const TermsAndServices = () => {
  const title = 'Terms and Services – Blog Land';
  const description =
    'Read the Terms and Services of Blog Land, the community-driven blogging platform. Learn about user responsibilities, content ownership, prohibited activities, and more.';
  const canonicalUrl = window.location.href;

  const structuredData = {
    '@context': 'https://schema.org',
    '@type': 'WebPage',
    name: title,
    description: description,
    url: canonicalUrl,
  };

  return (
    <div className="container">
      <Helmet>
        <title>{title}</title>
        <meta name="description" content={description} />
        <meta
          name="keywords"
          content="Blog Land, terms of service, user agreement, blogging rules, content ownership, prohibited activities"
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

      <div className={styles.LegalPages}>
        <h1>{title}</h1>

        <section>
          <h2>1. Acceptance of Terms</h2>
          <p>
            By accessing and using Blog Land, you agree to comply with and be bound by these Terms
            and Services. If you do not agree, please refrain from using our platform.
          </p>
        </section>

        <section>
          <h2>2. User Responsibilities</h2>
          <p>
            Users are responsible for maintaining the confidentiality of their account and agree to
            use Blog Land for lawful purposes only.
          </p>
        </section>

        <section>
          <h2>3. Content Ownership</h2>
          <p>
            Users retain ownership of the content they create. By posting on Blog Land, you grant us
            a non-exclusive license to display, distribute, and promote your content within our
            platform.
          </p>
        </section>

        <section>
          <h2>4. Prohibited Activities</h2>
          <p>
            You agree not to engage in activities that may harm Blog Land, including posting
            offensive content, attempting unauthorized access, or distributing malware.
          </p>
        </section>

        <section>
          <h2>5. Termination of Service</h2>
          <p>
            Blog Land reserves the right to suspend or terminate accounts that violate these terms
            or misuse the platform.
          </p>
        </section>

        <section>
          <h2>6. Changes to Terms</h2>
          <p>
            Blog Land may update these Terms and Services periodically. Continued use of the
            platform indicates acceptance of any changes.
          </p>
        </section>

        <section>
          <h2>7. Contact Information</h2>
          <p>
            For any questions about these Terms and Services, please contact us at{' '}
            <a href="mailto:info@blogland.com">info@blogland.com</a>.
          </p>
        </section>
      </div>
    </div>
  );
};
