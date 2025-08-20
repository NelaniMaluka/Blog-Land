import styles from '../components/common/LegalPages.module.css';

export const PrivacyPolicy = () => {
  return (
    <div className="container">
      <div className={styles.LegalPages}>
        <h1 className={styles.title}>Privacy Policy</h1>
        <p className={styles.text}>
          Your privacy is important to us at <span className={styles.highlight}>Blog Land</span>.
          This Privacy Policy explains how we collect, use, and protect your information when you
          use our platform.
        </p>

        <h2 className={styles.sectionTitle}>1. Information We Collect</h2>
        <ul className={styles.list}>
          <li>
            <span className={styles.highlight}>Personal Information:</span> such as your name, email
            address, and profile details when you create an account.
          </li>
          <li>
            <span className={styles.highlight}>Usage Data:</span> including interactions with the
            platform, posts you read, and features you use.
          </li>
          <li>
            <span className={styles.highlight}>Cookies:</span> to improve your browsing experience
            and personalize content.
          </li>
        </ul>

        <h2 className={styles.sectionTitle}>2. How We Use Your Information</h2>
        <p className={styles.text}>We use your information to:</p>
        <ul className={styles.list}>
          <li>Provide, maintain, and improve Blog Land’s features and services.</li>
          <li>Personalize your experience and recommend content.</li>
          <li>Communicate with you about updates, promotions, or security alerts.</li>
        </ul>

        <h2 className={styles.sectionTitle}>3. Sharing of Information</h2>
        <p className={styles.text}>
          We do not sell your personal data. Information may only be shared with trusted service
          providers (such as hosting or analytics) to help operate Blog Land, or if required by law.
        </p>

        <h2 className={styles.sectionTitle}>4. Data Security</h2>
        <p className={styles.text}>
          We take reasonable measures to protect your personal information. However, no method of
          electronic transmission or storage is 100% secure, so we cannot guarantee absolute
          security.
        </p>

        <h2 className={styles.sectionTitle}>5. Your Rights</h2>
        <ul className={styles.list}>
          <li>Access, update, or delete your personal information at any time.</li>
          <li>Opt-out of promotional emails or notifications.</li>
          <li>Request details about the data we collect and how it’s used.</li>
        </ul>

        <h2 className={styles.sectionTitle}>6. Changes to This Policy</h2>
        <p className={styles.text}>
          We may update this Privacy Policy from time to time. Updates will be posted on this page
          with a revised “Last Updated” date. Your continued use of Blog Land means you accept any
          changes.
        </p>

        <h2 className={styles.sectionTitle}>7. Contact Us</h2>
        <p className={styles.text}>
          If you have any questions or concerns about this Privacy Policy, contact us at{' '}
          <a href="mailto:info@blogland.com" className={styles.link}>
            info@blogland.com
          </a>
          .
        </p>
      </div>
    </div>
  );
};
