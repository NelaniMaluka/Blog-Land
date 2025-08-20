import styles from '../components/common/LegalPages.module.css';

export const TermsAndServices = () => {
  return (
    <div className="container">
      <div className={styles.LegalPages}>
        <h1>Terms and Services</h1>

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
