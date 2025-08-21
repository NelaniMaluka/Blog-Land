import React from 'react';
import styles from './PostCard.module.css';
import VisibilityIcon from '@mui/icons-material/Visibility';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';
import { ROUTES } from '../../constants/routes';
import { formatViews } from '../../utils/formatUtils';
import FallbackAvatars from '../common/Avatar';
import { PostResponse } from '../../types/post/response';

interface PostCardProps {
  post: PostResponse;
  categoryName?: string;
}

export const PostCard: React.FC<PostCardProps> = ({ post, categoryName }) => {
  return (
    <div key={post.id} className={styles.post}>
      <img src={post.postImgUrl} alt="img" />
      <div className={styles.subDetails}>
        {categoryName && (
          <a href={ROUTES.CATEGORY_POSTS(categoryName)} className={styles.category}>
            {categoryName}
          </a>
        )}
        <span>{formatViews(post.views)} views</span>
        <span>{post.readTime} min read</span>
      </div>
      <p className={styles.title}>{post.title}</p>
      <p>{post.summary}</p>

      <div className={styles.info}>
        <div className={styles.userInfo}>
          <div>
            <FallbackAvatars user={post.user} />
          </div>
          <div>
            <span>{post.user.firstname + ' ' + post.user.lastname}</span>
            <span className={styles.date}>{post.createdAt}</span>
          </div>
        </div>
        <div>
          <a href={ROUTES.POST(post.id)} className={styles.readMore}>
            Read more <ArrowForwardIcon className={styles.readMoreIcon} fontSize="small" />
          </a>
        </div>
      </div>
    </div>
  );
};
