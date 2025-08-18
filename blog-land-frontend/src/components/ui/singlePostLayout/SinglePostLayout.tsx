import { useGetPost } from '../../../hooks/usePost';
import LoadingScreen from '../../../features/LoadingScreen/LoadingScreen';
import { ROUTES } from '../../../constants/routes';
import { useGetCategories } from '../../../hooks/useCategory';
import VisibilityIcon from '@mui/icons-material/Visibility';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import { formatViews } from '../../../utils/formatUtils';
import LocationOnIcon from '@mui/icons-material/LocationOn';
import { formatDate } from '../../../utils/formatUtils';
import { ExperienceLabels } from '../../../types/user/response';

import styles from './SinglePostLayout.module.css';
import { PostResponse } from '../../../types/post/response';

interface SinglePostLayoutProps {
  post?: PostResponse;
  isLoading: boolean;
  isError?: boolean;
}

export const SinglePostLayout = ({ post, isLoading, isError }: SinglePostLayoutProps) => {
  const { data: categoriesData } = useGetCategories();

  const category = categoriesData?.find((c) => c.id === post?.categoryId);
  const categoryName = category?.name;

  if (isError) return <div>Error loading post</div>;

  return (
    <>
      <LoadingScreen isLoading={isLoading}>
        <div className="container">
          <div className={styles.holder}>
            <div className={styles.column1}>
              <img src={post?.postImgUrl} alt={post?.title} className={styles.img} />
              {categoryName && (
                <a href={ROUTES.CATEGORY_POSTS(categoryName)} className={styles.category}>
                  {categoryName}
                </a>
              )}
              <span className={styles.date}>{post?.createdAt}</span>
              <h2 className={styles.title}>{post?.title}</h2>
              <p>{post?.summary}</p>
              <div className={styles.subDetails}>
                <span>
                  <VisibilityIcon fontSize="small" className={styles.icon} />{' '}
                  {formatViews(post?.views ?? 0)}
                </span>
                <span>
                  <AccessTimeIcon fontSize="small" className={styles.icon} /> {post?.readTime} min
                  read
                </span>
              </div>
              <div
                dangerouslySetInnerHTML={{ __html: post?.content || '' }}
                className={styles.content}
              />
            </div>
            {post?.user && (
              <div className={styles.column2}>
                <p className={styles.name}>{post?.user?.firstname + ' ' + post?.user?.lastname}</p>
                <p className={styles.title1}>{post?.user?.title && post?.user?.title}</p>
                <p className={styles.experience}>
                  Experience:{' '}
                  {post?.user?.experience ? ExperienceLabels[post.user.experience] : 'N/A'}
                </p>

                <p className={styles.location}>
                  {post?.user?.location && (
                    <>
                      <LocationOnIcon fontSize="small" className={styles.locationIcon} />
                      {post?.user?.location}
                    </>
                  )}
                </p>
                <p className={styles.summary}>{post?.user?.summary && post?.user?.summary}</p>

                <p className={styles.joinAt}>joined on {formatDate(post?.user?.joinedAt)}</p>
                <p>{}</p>
              </div>
            )}
          </div>
        </div>
      </LoadingScreen>
    </>
  );
};
