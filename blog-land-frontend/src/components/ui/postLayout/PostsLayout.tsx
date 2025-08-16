import { useState, useEffect } from 'react';
import { PostCard } from '../../cards/postCard';
import LoadingScreen from '../../../features/LoadingScreen/LoadingScreen';
import styles from './PostLayout.module.css';
import { Order, PostResponse, PaginatedPosts } from '../../../types/post/response';
import { useGetCategoryPosts } from '../../../hooks/usePost';

interface PostsLayoutProps {
  title: string;
  showOrderButtons?: boolean;
  defaultOrder?: Order;
  categoryId?: number;
  fetchFn?: (params?: { order?: Order; page?: number }) => {
    data?: PaginatedPosts;
    isLoading: boolean;
    isError: boolean;
  };
}

export const PostsLayout: React.FC<PostsLayoutProps> = ({
  title,
  showOrderButtons = true,
  defaultOrder = Order.LATEST,
  categoryId,
  fetchFn,
}) => {
  const [order, setOrder] = useState<Order>(defaultOrder);
  const [page, setPage] = useState<number>(0);

  // Reset page & order if category or default changes
  useEffect(() => {
    setOrder(defaultOrder);
    setPage(0);
  }, [categoryId, defaultOrder]);

  const queryResult = categoryId
    ? useGetCategoryPosts({ categoryId, page, size: 12, order })
    : fetchFn
    ? fetchFn({ order, page })
    : {
        data: { content: [], totalPages: 0, totalElements: 0, number: 0 },
        isLoading: false,
        isError: false,
      };

  const { data, isLoading, isError } = queryResult;

  const posts: PostResponse[] = data?.content ?? [];
  const totalPages: number = data?.totalPages ?? 0;

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

  if (!isLoading && posts.length === 0)
    return (
      <div className="container">
        <div className={styles.holder}>
          <div className={styles.header}>
            <h2>{title}</h2>
          </div>
          <div className={styles.message}>No posts yet. Be the first!</div>
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
                onClick={() => {
                  setOrder(Order.LATEST);
                  setPage(0);
                }}
              >
                Latest
              </div>
              <div
                className={`${styles.toggleOption} ${order === Order.OLDEST ? styles.active : ''}`}
                onClick={() => {
                  setOrder(Order.OLDEST);
                  setPage(0);
                }}
              >
                Oldest
              </div>
            </div>
          )}
        </div>

        <LoadingScreen isLoading={isLoading}>
          <div className={styles.postsGrid}>
            {posts.map((post: PostResponse) => (
              <PostCard key={post.id} post={post} categoryName={post.categoryName} />
            ))}
          </div>

          {/* Pagination Controls */}
          {totalPages > 1 && (
            <div className={styles.pagination}>
              <button onClick={() => setPage((p) => p - 1)} disabled={page === 0}>
                Prev
              </button>
              <span>
                Page {page + 1} of {totalPages}
              </span>
              <button onClick={() => setPage((p) => p + 1)} disabled={page + 1 >= totalPages}>
                Next
              </button>
            </div>
          )}
        </LoadingScreen>
      </div>
    </div>
  );
};
