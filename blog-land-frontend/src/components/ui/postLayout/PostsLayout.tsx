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

  if (isError)
    return (
      <div className="container">
        <BasicBreadcrumbs title="Post" link={ROUTES.VIEW_ALL} page={title} />
        <div className={styles.holder}>
          <div className={styles.header}>
            <h3>posts: {totalElements}</h3>
          </div>
          <div className={styles.message}>Could not load data.</div>
        </div>
      </div>
    );

  if (!isLoading && posts.length === 0)
    return (
      <div className="container">
        <BasicBreadcrumbs title="Post" link={ROUTES.VIEW_ALL} page={title} />
        <div className={styles.holder}>
          <div className={styles.header}>
            <h3>posts: {totalElements}</h3>
          </div>
          <div className={styles.message}>No posts yet. Be the first!</div>
        </div>
      </div>
    );

  return (
    <div className="container">
      <BasicBreadcrumbs title="Post" link={ROUTES.VIEW_ALL} page={title} />
      <div className={styles.holder}>
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

        <LoadingScreen isLoading={isLoading}>
          <div className={styles.postsGrid}>
            {posts.map((post) => {
              const category = categoriesData?.find((c) => c.id === post.categoryId);
              return <PostCard key={post.id} post={post} categoryName={category?.name} />;
            })}
          </div>

          {/* MUI Pagination */}
          {totalPages > 1 && (
            <div className={styles.pagination}>
              <Pagination
                page={page + 1} // MUI is 1-based, your state is 0-based
                count={totalPages}
                onChange={(_, value) => {
                  setPage(value - 1); // convert back to 0-based
                  window.scrollTo({ top: 0, behavior: 'smooth' });
                }}
                renderItem={(item) => <PaginationItem {...item} />}
              />
            </div>
          )}
        </LoadingScreen>
      </div>
    </div>
  );
};
