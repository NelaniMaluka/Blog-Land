import styles from './LatestSection.module.css';
import { useGetAllPost } from '../../../../hooks/usePost';
import { useGetCategories } from '../../../../hooks/useCategory';
import { Order } from '../../../../types/post/response';
import LoadingScreen from '../../../../features/LoadingScreen/LoadingScreen';
import { ROUTES } from '../../../../constants/routes';
import { PostCard } from '../../../cards/PostCard';
import VisibilityIcon from '@mui/icons-material/Visibility';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';
import { formatViews } from '../../../../utils/formatUtils';

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

  // Safe access
  const recentPost = recentData?.content?.[0];
  const category = recentPost
    ? categoriesData?.find((c) => c.id === recentPost.categoryId)?.name || 'Unknown'
    : undefined;

  if (!recentPost) return null; // nothing to render yet

  return (
    <div className="container">
      <LoadingScreen isLoading={recentLoading || categoriesLoading}>
        <div className={styles.latestContainer}>
          <div className={styles.row1}>
            <h2>Most Recent</h2>
            <a href={ROUTES.VIEW_ALL}>View All</a>
          </div>

          <div className={styles.featuredPost}>
            <img src={recentPost.postImgUrl} alt="img" className={styles.featuredImg} />
            <div className={styles.featuredContent}>
              {category && (
                <a href={`/category/${encodeURIComponent(category)}`} className={styles.category}>
                  {category}
                </a>
              )}
              <span className={styles.date}>{recentPost.createdAt}</span>
              <p className={styles.title}>{recentPost.title}</p>
              <p>{recentPost.summary}</p>
              <div className={styles.subDetails}>
                <span>
                  <VisibilityIcon fontSize="small" /> {formatViews(recentPost.views)}
                </span>
                <span>
                  <AccessTimeIcon fontSize="small" /> {recentPost.readTime} min read
                </span>
              </div>
              <a href={ROUTES.POST(recentPost.id)} className={styles.readMore}>
                Read more <ArrowForwardIcon className={styles.readMoreIcon} fontSize="small" />
              </a>
            </div>
          </div>

          <div className={styles.gridPosts}>
            {recentData?.content.slice(1).map((post) => {
              const postCategory = categoriesData?.find((c) => c.id === post.categoryId);
              return <PostCard key={post.id} post={post} categoryName={postCategory?.name} />;
            })}
          </div>
        </div>
      </LoadingScreen>
    </div>
  );
};
