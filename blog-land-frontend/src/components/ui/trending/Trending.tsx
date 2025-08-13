import styles from './TrendingSection.module.css';
import { useGetTrendingPosts } from '../../../hooks/usePost';
import LoadingScreen from '../../../features/LoadingScreen/LoadingScreen';
import VisibilityIcon from '@mui/icons-material/Visibility';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import { formatViews } from '../../../utils/formatUtils';
import { useGetCategories } from '../../../hooks/useCategory';
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
    <>
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

                return (
                  <div key={post.id} className={styles.post}>
                    <img src={post.postImgUrl} alt="img" />
                    {category && <span className={styles.category}>{category.name}</span>}
                    <span className={styles.date}>{post.createdAt}</span>
                    <p className={styles.title}>{post.title}</p>
                    <p>{post.summary}</p>
                    <div className={styles.subDetails}>
                      <span>
                        <VisibilityIcon fontSize="small" className={styles.icon} />{' '}
                        {formatViews(post.views)}
                      </span>
                      <span>
                        <AccessTimeIcon fontSize="small" className={styles.icon} /> {post.readTime}{' '}
                        min read
                      </span>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        </div>
      </LoadingScreen>
    </>
  );
};
