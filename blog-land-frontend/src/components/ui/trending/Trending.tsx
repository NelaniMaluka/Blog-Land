import styles from './TrendingSection.module.css';
import { useGetTrendingPosts } from '../../../hooks/usePost';
import { useGetCategories } from '../../../hooks/useCategory';
import LoadingScreen from '../../../features/LoadingScreen/LoadingScreen';
import { PostCard } from '../../cards/postCard';
import { ROUTES } from '../../../constants/routes';

export const TrendingSection = () => {
  const {
    data: trendingData,
    isLoading: trendingLoading,
    error: trendingError,
  } = useGetTrendingPosts({ page: 0, size: 6 });

  const {
    data: categoriesData,
    isLoading: categoriesLoading,
    error: categoriesError,
  } = useGetCategories();

  if (!trendingData?.length) return null;

  return (
    <LoadingScreen isLoading={trendingLoading || categoriesLoading}>
      <div className={styles.videoContainer}>
        <div className="container">
          <div className={styles.row1}>
            <h2>Trending</h2>
            <a href={ROUTES.TRENDING_POSTS}>View All</a>
          </div>

          <div className={styles.row2}>
            {trendingData.map((post) => {
              const category = categoriesData?.find((c) => c.id === post.categoryId);
              return <PostCard key={post.id} post={post} categoryName={category?.name} />;
            })}
          </div>
        </div>
      </div>
    </LoadingScreen>
  );
};
