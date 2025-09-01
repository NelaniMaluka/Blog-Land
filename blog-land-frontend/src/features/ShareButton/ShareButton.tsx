import { useState } from 'react';
import { FaWhatsapp, FaFacebook, FaTwitter, FaLinkedin, FaLink } from 'react-icons/fa';
import { MdShare } from 'react-icons/md';
import styles from './ShareButton.module.css';
import { PostResponse } from '../../types/post/response';
import { slugify } from '../../utils/formatUtils';

interface ShareFeatureProps {
  post: PostResponse;
}

export default function ShareFeature({ post }: ShareFeatureProps) {
  const [open, setOpen] = useState(false);

  // Use post info instead of window/document
  const currentUrl = `${window.location.origin}/post/${post.id}-${slugify(post.title)}`;
  const pageTitle = post.title;
  const pageSummary = post.summary || '';
  const pageImage = post.postImgUrl || '/thumbnail.jpg';

  const shareOptions = [
    {
      name: 'WhatsApp',
      icon: '/icons/whatsapp.png',
      url: `https://wa.me/?text=${encodeURIComponent(pageTitle + ' ' + currentUrl)}`,
    },
    {
      name: 'Facebook',
      icon: '/icons/facebook.png',
      url: `https://www.facebook.com/sharer/sharer.php?u=${encodeURIComponent(
        currentUrl
      )}&quote=${encodeURIComponent(pageSummary)}`,
    },
    {
      name: 'Twitter',
      icon: '/icons/twitter.png',
      url: `https://twitter.com/intent/tweet?url=${encodeURIComponent(
        currentUrl
      )}&text=${encodeURIComponent(pageTitle)}`,
    },
    {
      name: 'LinkedIn',
      icon: '/icons/linkedin.png',
      url: `https://www.linkedin.com/shareArticle?mini=true&url=${encodeURIComponent(
        currentUrl
      )}&title=${encodeURIComponent(pageTitle)}&summary=${encodeURIComponent(pageSummary)}`,
    },
  ];

  const copyLink = async () => {
    try {
      await navigator.clipboard.writeText(currentUrl);
    } catch (err) {}
  };

  return (
    <div>
      {/* Share button */}
      <button onClick={() => setOpen(true)} className={styles.shareButton}>
        <img src="/icons/share.png" alt="Share Icon" />
        Share
      </button>

      {/* Modal */}
      {open && (
        <div className={styles.modalOverlay}>
          <div className={styles.modalContent}>
            <button onClick={() => setOpen(false)} className={styles.closeButton}>
              ✕
            </button>

            {/* Share buttons */}
            <div className={styles.shareCont}>
              {' '}
              Share <hr />
              <div className={styles.shareOptions}>
                {shareOptions.map((option) => (
                  <a
                    key={option.name}
                    href={option.url}
                    target="_blank"
                    rel="noopener noreferrer"
                    className={styles.shareOption}
                  >
                    <img src={option.icon} alt="Icon" />
                    {option.name}
                  </a>
                ))}
              </div>
            </div>

            {/* Copy link */}
            <div className={styles.copyCont}>
              <div>
                <FaLink />
                <p>{currentUrl}</p>
              </div>

              <button onClick={copyLink} className={styles.copyButton}>
                Copy
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
