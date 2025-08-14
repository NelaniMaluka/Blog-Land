import { useState } from 'react';
import { PostCard } from '../../cards/postCard';
import LoadingScreen from '../../../features/LoadingScreen/LoadingScreen';
import styles from './PostLayout.module.css';
import { Order } from '../../../types/post/response';

interface PostsLayoutProps {
  title: string;
  fetchFn: (params?: { order?: Order }) => any;
  defaultOrder?: Order;
  showOrderButtons?: boolean;
}

export const PostsLayout: React.FC<PostsLayoutProps> = ({
  title,
  fetchFn,
  defaultOrder = Order.LATEST,
  showOrderButtons = true,
}) => {
  const [order, setOrder] = useState<Order>(defaultOrder);

  // If ordering is relevant, pass it; otherwise pass nothing
  const fetchParams = showOrderButtons ? { order } : undefined;
  const { data: posts, isLoading } = fetchFn(fetchParams);

  return (
    <div className="container">
      <div className={styles.holder}>
        <div className={styles.header}>
          <h2>{title}</h2>
          {showOrderButtons && (
            <div className={styles.toggleGroup}>
              <div
                className={`${styles.toggleOption} ${order === Order.LATEST ? styles.active : ''}`}
                onClick={() => setOrder(Order.LATEST)}
              >
                Latest
              </div>
              <div
                className={`${styles.toggleOption} ${order === Order.OLDEST ? styles.active : ''}`}
                onClick={() => setOrder(Order.OLDEST)}
              >
                Oldest
              </div>
            </div>
          )}
        </div>

        <LoadingScreen isLoading={isLoading}>
          <div className={styles.postsGrid}>
            {posts?.map((post: any) => (
              <PostCard key={post.id} post={post} categoryName={post.categoryName} />
            ))}
          </div>
        </LoadingScreen>
      </div>
    </div>
  );
};
