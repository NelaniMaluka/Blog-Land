import React from 'react';
import styles from './PostCard.module.css';
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';
import { ROUTES } from '../../constants/routes';
import { formatDigit } from '../../utils/formatUtils';
import FallbackAvatars from '../common/Avatar';
import { PostResponse } from '../../types/post/response';
import { useGetPublicUser } from '../../hooks/useUser';

interface PostCardProps {
  post: PostResponse;
  categoryName?: string;
}

export const PostCard: React.FC<PostCardProps> = ({ post, categoryName }) => {
  const { data: user } = useGetPublicUser(post.userId);

  return (
    <article key={post.id} className={styles.post}>
      {/* image */}
      <img src={post.postImgUrl} alt="img" />

      {/* views, category, read-time  */}
      <div className={styles.subDetails}>
        <span>{formatDigit(post.views)} views</span>
        {categoryName && (
          <a href={ROUTES.CATEGORY_POSTS(categoryName)} className={styles.category}>
            {categoryName}
          </a>
        )}
        <span>{post.readTime} min read</span>
      </div>

      {/* Title and Summary */}
      <p className={styles.title}>{post.title}</p>
      <p>{post.summary}</p>

      {/* User and Read-more */}
      <div className={styles.info}>
        <div className={styles.userInfo}>
          <div>
            <FallbackAvatars user={user ?? null} />
          </div>
          <div>
            <span>{user?.firstname + ' ' + user?.lastname}</span>
            <span className={styles.date}>{post.createdAt}</span>
          </div>
        </div>
        <div>
          <a href={ROUTES.POST(post.id, post.title)} className={styles.readMore}>
            Read more <ArrowForwardIcon className={styles.readMoreIcon} fontSize="small" />
          </a>
        </div>
      </div>
    </article>
  );
};
