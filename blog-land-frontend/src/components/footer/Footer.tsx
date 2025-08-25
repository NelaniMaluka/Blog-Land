import React, { useState } from 'react';
import styles from './Footer.module.css';
import {
  FaLinkedin,
  FaFacebookF,
  FaInstagram,
  FaGithub,
  FaHome,
  FaEnvelope,
  FaPhone,
} from 'react-icons/fa';
import { ROUTES } from '../../constants/routes';
import LoadingScreen from '../../features/LoadingScreen/LoadingScreen';
import { useNewsletterSubscription } from '../../hooks/useNewsletter';
import ErrorMessage from '../../features/Snackbars/errorMessage';
import SuccessMessage from '../../features/Snackbars/successMessage';

const Footer: React.FC = () => {
  const [email, setEmail] = useState('');

  const newsletter = useNewsletterSubscription();

  const handleSubscribe = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await newsletter.mutateAsync(email);
      setEmail('');
    } catch (error) {}
  };
  if (newsletter.isSuccess) {
    console.log(newsletter.data);
  }

  return (
    <footer className={styles.footer}>
      <LoadingScreen isLoading={newsletter.isPending}>
        <div className="container">
          {/* Social Section */}
          <div className={styles.socialSection}>
            <span className={styles.socialText}>Get connected with us on social networks:</span>
            <div className={styles.socialIcons}>
              <a
                href="https://www.linkedin.com"
                target="_blank"
                rel="noopener noreferrer"
                aria-label="LinkedIn"
              >
                <FaLinkedin className={styles.icon} />
              </a>
              <a
                href="https://www.facebook.com"
                target="_blank"
                rel="noopener noreferrer"
                aria-label="Facebook"
              >
                <FaFacebookF className={styles.icon} />
              </a>
              <a
                href="https://www.instagram.com"
                target="_blank"
                rel="noopener noreferrer"
                aria-label="Instagram"
              >
                <FaInstagram className={styles.icon} />
              </a>
              <a
                href="https://github.com"
                target="_blank"
                rel="noopener noreferrer"
                aria-label="GitHub"
              >
                <FaGithub className={styles.icon} />
              </a>
            </div>
          </div>

          {/* Main Footer */}
          <div className={styles.mainFooter}>
            <div className={styles.footerCol}>
              <a href={ROUTES.HOME} className={styles.logo}>
                <h2>Blog-Land</h2>
              </a>
              <p>
                Thanks for visiting our blog! Dive into our latest articles, explore popular topics,
                and stay connected for fresh insights every week.
              </p>
            </div>

            <div className={styles.footerCol}>
              <h5>Navigate</h5>
              <a href={ROUTES.ABOUT}>About</a>
              <a href={ROUTES.LATEST_POSTS}>Latest</a>
              <a href={ROUTES.TRENDING_POSTS}>Trending</a>
              <a href={ROUTES.RANDOM_POSTS}>Random</a>
            </div>

            <div className={styles.footerCol}>
              <h5>Contact</h5>

              <a href="mailto:info@blog-land.com">info@blog-land.com</a>

              <a href="tel:012345678">012 345 6788</a>
            </div>
            {/* Newsletter Section */}
            <div className={styles.footerCol}>
              <h5>Subscribe to Newsletter</h5>
              <form onSubmit={handleSubscribe} className={styles.newsletterForm}>
                <input
                  type="email"
                  placeholder="Enter your email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
                />
                <button type="submit">Subscribe</button>
              </form>
              <p>Get the latest posts delivered straight to your inbox.</p>
            </div>
          </div>

          {/* Copyright */}
          <div className={styles.copyRight}>
            <div>© 2025 Copyright Blog-Land</div>
            <div>
              <a href={ROUTES.PRIVACY_POLICY}>Privacy</a>
              <a href={ROUTES.TERMS_AND_CONDITIONS}>Terms</a>
            </div>
          </div>
        </div>
      </LoadingScreen>
      {newsletter.isSuccess && <SuccessMessage message={newsletter.data} />}
      {newsletter.isError && (
        <ErrorMessage message={newsletter?.error?.message || 'Something went wrong'} />
      )}
    </footer>
  );
};

export default Footer;
