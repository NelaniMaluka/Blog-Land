import { useState } from 'react';
import { FaWhatsapp, FaFacebook, FaTwitter, FaLinkedin, FaLink } from 'react-icons/fa';
import { MdShare } from 'react-icons/md';
import styles from './ShareButton.module.css';
import { PostResponse } from '../../types/post/response';

interface ShareFeatureProps {
  post: PostResponse;
}

export default function ShareFeature({ post }: ShareFeatureProps) {
  const [open, setOpen] = useState(false);

  // Use post info instead of window/document
  const currentUrl = `${window.location.origin}/posts/${post.id}`;
  const pageTitle = post.title;
  const pageSummary = post.summary || '';
  const pageImage = post.postImgUrl || '/thumbnail.jpg';

  const shareOptions = [
    {
      name: 'WhatsApp',
      icon: <FaWhatsapp className="text-green-500 text-2xl" />,
      url: `https://wa.me/?text=${encodeURIComponent(pageTitle + ' ' + currentUrl)}`,
    },
    {
      name: 'Facebook',
      icon: <FaFacebook className="text-blue-600 text-2xl" />,
      url: `https://www.facebook.com/sharer/sharer.php?u=${encodeURIComponent(
        currentUrl
      )}&quote=${encodeURIComponent(pageSummary)}`,
    },
    {
      name: 'Twitter',
      icon: <FaTwitter className="text-sky-500 text-2xl" />,
      url: `https://twitter.com/intent/tweet?url=${encodeURIComponent(
        currentUrl
      )}&text=${encodeURIComponent(pageTitle)}`,
    },
    {
      name: 'LinkedIn',
      icon: <FaLinkedin className="text-blue-700 text-2xl" />,
      url: `https://www.linkedin.com/shareArticle?mini=true&url=${encodeURIComponent(
        currentUrl
      )}&title=${encodeURIComponent(pageTitle)}&summary=${encodeURIComponent(pageSummary)}`,
    },
  ];

  const copyLink = async () => {
    try {
      await navigator.clipboard.writeText(currentUrl);
      alert('Link copied!');
    } catch (err) {
      console.error('Failed to copy: ', err);
    }
  };

  return (
    <div>
      {/* Share button */}
      <button onClick={() => setOpen(true)} className={styles.shareButton}>
        <MdShare className="text-xl" />
        <span>Share</span>
      </button>

      {/* Modal */}
      {open && (
        <div className={styles.modalOverlay}>
          <div className={styles.modalContent}>
            <button onClick={() => setOpen(false)} className={styles.closeButton}>
              ✕
            </button>

            {/* Preview */}
            <div className={styles.preview}>
              <img src={pageImage} alt={pageTitle} />
              <div>
                <h3>{pageTitle}</h3>
                <p>{currentUrl}</p>
              </div>
            </div>

            {/* Share buttons */}
            <div className={styles.shareOptions}>
              {shareOptions.map((option) => (
                <a
                  key={option.name}
                  href={option.url}
                  target="_blank"
                  rel="noopener noreferrer"
                  className={styles.shareOption}
                >
                  {option.icon}
                  <span>{option.name}</span>
                </a>
              ))}
            </div>

            {/* Copy link */}
            <button onClick={copyLink} className={styles.copyButton}>
              <FaLink />
              Copy link
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
