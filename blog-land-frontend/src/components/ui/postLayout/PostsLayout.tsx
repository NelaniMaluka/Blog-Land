import { useState, useEffect } from 'react';
import { PostCard } from '../../cards/postCard';
import LoadingScreen from '../../../features/LoadingScreen/LoadingScreen';
import styles from './PostLayout.module.css';
import { Order } from '../../../types/post/response';
import { useGetCategoryPosts } from '../../../hooks/usePost';

interface PostsLayoutProps {
  title: string;
  showOrderButtons?: boolean;
  defaultOrder?: Order;
  categoryId?: number;
  fetchFn?: (params?: { order?: Order }) => { data?: any[]; isLoading: boolean; isError: boolean };
}

export const PostsLayout: React.FC<PostsLayoutProps> = ({
  title,
  showOrderButtons = true,
  defaultOrder = Order.LATEST,
  categoryId,
  fetchFn,
}) => {
  const [order, setOrder] = useState<Order>(defaultOrder);

  useEffect(() => setOrder(defaultOrder), [categoryId, defaultOrder]);

  const queryResult = categoryId
    ? useGetCategoryPosts({ categoryId, page: 0, size: 12, order })
    : fetchFn
    ? fetchFn({ order })
    : { data: [], isLoading: false, isError: false };

  const { data: posts, isLoading, isError } = queryResult;

  if (isError)
    return (
      <div className="container">
        <div className={styles.holder}>
          <div className={styles.header}>
            <h2>{title}</h2>
          </div>
          <div className={styles.message}>Could not load data.</div>
        </div>
      </div>
    );

  if (!isLoading && (!posts || posts.length === 0))
    return (
      <div className="container">
        <div className={styles.holder}>
          <div className={styles.header}>
            <h2>{title}</h2>
          </div>
          <div className={styles.message}>No posts yet. Be the first!</div>{' '}
        </div>
      </div>
    );

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

        <LoadingScreen isLoading={isLoading ?? false}>
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
