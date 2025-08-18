import { PostCard } from '../../cards/postCard';
import LoadingScreen from '../../../features/LoadingScreen/LoadingScreen';
import styles from './PostLayout.module.css';
import { Order, PostResponse } from '../../../types/post/response';
import { useGetCategories } from '../../../hooks/useCategory';
import Pagination from '@mui/material/Pagination';
import PaginationItem from '@mui/material/PaginationItem';
import BasicBreadcrumbs from '../../breadcrumbs/breadcrumbs';
import { ROUTES } from '../../../constants/routes';

interface PostsLayoutProps {
  title: string;
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
        <Pagination
          page={page + 1}
          count={totalPages}
          onChange={(_, value) => {
            setPage(value - 1);
            window.scrollTo({ top: 0, behavior: 'smooth' });
          }}
          renderItem={(item) => <PaginationItem {...item} />}
        />
      </div>
    );

  const renderErrorOrEmpty = (message: string) => (
    <div className="container">
      <BasicBreadcrumbs title="Post" link={ROUTES.VIEW_ALL} page={title} />
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
    <div className="container">
      <BasicBreadcrumbs title="Post" link={ROUTES.VIEW_ALL} page={title} />
      <div className={styles.holder}>
        {renderHeader()}
        <LoadingScreen isLoading={isLoading}>
          {renderPosts()}
          {renderPagination()}
        </LoadingScreen>
      </div>
    </div>
  );
};
