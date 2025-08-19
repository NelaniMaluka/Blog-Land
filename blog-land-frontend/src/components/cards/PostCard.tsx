import React from 'react';
import styles from './PostCard.module.css';
import VisibilityIcon from '@mui/icons-material/Visibility';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';
import { ROUTES } from '../../constants/routes';
import { formatViews } from '../../utils/formatUtils';

interface PostCardProps {
  post: any;
  categoryName?: string;
}

export const PostCard: React.FC<PostCardProps> = ({ post, categoryName }) => {
  return (
    <div key={post.id} className={styles.post}>
      <img src={post.postImgUrl} alt="img" />
      {categoryName && (
        <a href={ROUTES.CATEGORY_POSTS(categoryName)} className={styles.category}>
          {categoryName}
        </a>
      )}
      <span className={styles.date}>{post.createdAt}</span>
      <p className={styles.title}>{post.title}</p>
      <p>{post.summary}</p>
      <div className={styles.subDetails}>
        <span>
          <VisibilityIcon fontSize="small" className={styles.icon} /> {formatViews(post.views)}
        </span>
        <span>
          <AccessTimeIcon fontSize="small" className={styles.icon} /> {post.readTime} min read
        </span>
      </div>
      <a href={ROUTES.POST(post.id)} className={styles.readMore}>
        Read more <ArrowForwardIcon className={styles.readMoreIcon} fontSize="small" />
      </a>
    </div>
  );
};
