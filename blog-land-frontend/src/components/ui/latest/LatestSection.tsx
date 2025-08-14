import styles from './LatestSection.module.css';
import { useGetAllPost } from '../../../hooks/usePost';
import { useGetCategories } from '../../../hooks/useCategory';
import { Order } from '../../../types/post/response';
import LoadingScreen from '../../../features/LoadingScreen/LoadingScreen';
import { ROUTES } from '../../../constants/routes';
import { PostCard } from '../../cards/postCard';
import VisibilityIcon from '@mui/icons-material/Visibility';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';
import { formatViews } from '../../../utils/formatUtils';

export const LatestSection = () => {
  const {
    data: recentData,
    isLoading: recentLoading,
    error: recentError,
  } = useGetAllPost({ page: 0, size: 4, order: Order.LATEST });

  const {
    data: categoriesData,
    isLoading: categoriesLoading,
    error: categoriesError,
  } = useGetCategories();

  if (!recentData?.length) return null;

  return (
    <div className="container">
      <LoadingScreen isLoading={recentLoading || categoriesLoading}>
        <div className={styles.latestContainer}>
          <div className={styles.row1}>
            <h2>Most Recent</h2>
            <a href={ROUTES.VIEW_ALL}>View All</a>
          </div>

          {recentData?.length > 0 && (
            <>
              {/* First post - big layout */}{' '}
              <div className={styles.featuredPost}>
                <img src={recentData[0].postImgUrl} alt="img" className={styles.featuredImg} />{' '}
                <div className={styles.featuredContent}>
                  {categoriesData && (
                    <span className={styles.category}>
                      {categoriesData.find((c) => c.id === recentData[0].categoryId)?.name}{' '}
                    </span>
                  )}
                  <span className={styles.date}>{recentData[0].createdAt}</span>{' '}
                  <p className={styles.title}>{recentData[0].title}</p>{' '}
                  <p>{recentData[0].summary}</p>{' '}
                  <div className={styles.subDetails}>
                    <span>
                      <VisibilityIcon fontSize="small" /> {formatViews(recentData[0].views)}{' '}
                    </span>
                    <span>
                      <AccessTimeIcon fontSize="small" /> {recentData[0].readTime} min read{' '}
                    </span>
                  </div>
                  <a href={ROUTES.POST(recentData[0].id)} className={styles.readMore}>
                    Read more <ArrowForwardIcon className={styles.readMoreIcon} fontSize="small" />
                  </a>
                </div>
              </div>
              {/* Next 3 posts - grid layout */}
              <div className={styles.gridPosts}>
                {recentData.slice(1).map((post) => {
                  const category = categoriesData?.find((c) => c.id === post.categoryId);
                  return <PostCard key={post.id} post={post} categoryName={category?.name} />;
                })}
              </div>
            </>
          )}
        </div>
      </LoadingScreen>
    </div>
  );
};
