import LoadingScreen from '../../../features/LoadingScreen/LoadingScreen';
import { ROUTES } from '../../../constants/routes';
import { useGetCategories } from '../../../hooks/useCategory';
import { formatDigit } from '../../../utils/formatUtils';
import BasicBreadcrumbs from '../../breadcrumbs/breadcrumbs';
import FallbackAvatars from '../../common/Avatar';
import ShareFeature from '../../../features/ShareButton/ShareButton';
import LikeButton from '../../../features/likeButton/LikeButton';

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

  if (isError)
    return (
      <div className="container">
        <div className={styles.message}>Could not load data.</div>
      </div>
    );

  return (
    <>
      <LoadingScreen isLoading={isLoading}>
        <div className="container">
          <BasicBreadcrumbs
            page1={categoryName || ''}
            link1={ROUTES.CATEGORY_POSTS(categoryName || '')}
            page2={post?.title}
          />
          <div className={styles.holder}>
            <div className={styles.column1}>
              <img src={post?.postImgUrl} alt={post?.title} className={styles.img} />
              <div className={styles.subDetails}>
                <span>{formatDigit(post?.views ?? 0)} views</span>
                {categoryName && (
                  <a href={ROUTES.CATEGORY_POSTS(categoryName)} className={styles.category}>
                    {categoryName}
                  </a>
                )}
                <span>{post?.readTime} min read</span>
              </div>
              <h2 className={styles.title}>{post?.title}</h2>
              <p>{post?.summary}</p>
              {post?.user && (
                <div className={styles.info}>
                  <div className={styles.userInfo}>
                    <FallbackAvatars user={post!.user} />
                    <div>
                      <p>{post?.user.firstname + ' ' + post?.user.lastname}</p>
                      <p className={styles.date}>{post?.createdAt}</p>
                    </div>
                  </div>
                  <div className={styles.featCont}>
                    <LikeButton postId={post.id} />
                    <ShareFeature post={post} />
                  </div>
                </div>
              )}
              <div
                dangerouslySetInnerHTML={{ __html: post?.content || '' }}
                className={styles.content}
              />
            </div>
            <div className={styles.column2}></div>
          </div>
        </div>
      </LoadingScreen>
    </>
  );
};
