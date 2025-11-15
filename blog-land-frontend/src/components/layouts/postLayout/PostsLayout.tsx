import { PostCard } from '../../cards/PostCard';
import LoadingScreen from '../../../features/LoadingScreen/LoadingScreen';
import styles from './PostsLayout.module.css';
import { Order, PostResponse } from '../../../types/post/response';
import { useGetCategories } from '../../../hooks/useCategory';
import BasicBreadcrumbs from '../../breadcrumbs/breadcrumbs';
import CustomPagination from '../../buttons/Pagination/Pagination';

interface PostsLayoutProps {
  title?: string;
  showOrderButtons?: boolean;
  posts: PostResponse[];
  isLoading: boolean;
  isError: boolean;
  page: number;
  setPage: (page: number) => void;
  totalPages: number;
  order: Order;
  setOrder: (order: Order) => void;
  totalElements: number;
}

export const PostsLayout: React.FC<PostsLayoutProps> = ({
  title,
  showOrderButtons = true,
  posts,
  isLoading,
  isError,
  page,
  setPage,
  totalPages,
  order,
  setOrder,
  totalElements,
}) => {
  const { data: categoriesData } = useGetCategories();

  // ----- Render Helpers -----
  const renderHeader = () => (
    <div className={styles.header}>
      <h3>posts: {totalElements}</h3>
      {showOrderButtons && (
        <div className={styles.toggleGroup}>
          <div
            className={`${styles.toggleOption} ${order === Order.LATEST ? styles.active : ''}`}
            onClick={() => setOrder(Order.LATEST)} // delegate to parent
          >
            Latest
          </div>
          <div
            className={`${styles.toggleOption} ${order === Order.OLDEST ? styles.active : ''}`}
            onClick={() => setOrder(Order.OLDEST)} // delegate to parent
          >
            Oldest
          </div>
        </div>
      )}
    </div>
  );

  const renderPosts = () => (
    <div className={styles.postsGrid}>
      {posts.map((post) => {
        const category = categoriesData?.find((c) => c.id === post.categoryId);
        return <PostCard key={post.id} post={post} categoryName={category?.name} />;
      })}
    </div>
  );

  const renderPagination = () =>
    totalPages > 1 && (
      <div className={styles.pagination}>
        <CustomPagination page={page} setPage={setPage} totalPages={totalPages} />
      </div>
    );

  const renderErrorOrEmpty = (message: string) => (
    <div className="container">
      <BasicBreadcrumbs page1={title} />
      <div className={styles.holder}>
        {renderHeader()}
        <div className={styles.message}>{message}</div>
      </div>
    </div>
  );

  // ----- Early returns -----
  if (isError) return renderErrorOrEmpty('Could not load data.');
  if (!isLoading && posts.length === 0) return renderErrorOrEmpty('No posts yet. Be the first!');

  // ----- Main return -----
  return (
    <section className="container">
      <BasicBreadcrumbs page1={title} />
      <div className={styles.holder}>
        {renderHeader()}
        <LoadingScreen isLoading={isLoading}>
          {renderPosts()}
          {renderPagination()}
        </LoadingScreen>
      </div>
    </section>
  );
};
