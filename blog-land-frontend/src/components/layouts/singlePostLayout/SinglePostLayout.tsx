import LoadingScreen from '../../../features/LoadingScreen/LoadingScreen';
import { ROUTES } from '../../../constants/routes';
import { useGetCategories } from '../../../hooks/useCategory';
import { formatDigit } from '../../../utils/formatUtils';
import BasicBreadcrumbs from '../../breadcrumbs/breadcrumbs';
import FallbackAvatars from '../../common/Avatar';
import ShareFeature from '../../../features/ShareButton/ShareButton';
import LikeButton from '../../../features/likeButton/LikeButton';
import { useGetRandomPosts } from '../../../hooks/usePost';
import { Link } from 'react-router-dom';
import { useEffect } from 'react';
import { useQueryClient } from '@tanstack/react-query';
import { useAddViewCount } from '../../../hooks/usePost';

import styles from './SinglePostLayout.module.css';
import { PostResponse } from '../../../types/post/response';

interface SinglePostLayoutProps {
  post?: PostResponse;
  isLoading: boolean;
  isError?: boolean;
  isLatest?: boolean;
}

export const SinglePostLayout = ({ post, isLoading, isError, isLatest }: SinglePostLayoutProps) => {
  const { data: categoriesData } = useGetCategories();
  const { data: randomPostsData } = useGetRandomPosts();
  const addView = useAddViewCount();
  const queryClient = useQueryClient();

  useEffect(() => {
    queryClient.invalidateQueries({ queryKey: ['randomPosts'] });
    if (post?.id) addView.mutateAsync(post.id);
  }, [post?.id, queryClient]);

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
            page1={categoryName || (isLatest ? 'Latest' : '')}
            link1={isLatest ? ROUTES.LATEST_POSTS : ROUTES.CATEGORY_POSTS(categoryName || '')}
            page2={post?.title}
          />
          <div className={styles.holder}>
            <div className={styles.column1}>
              <img src={post?.postImgUrl} alt={post?.title} className={styles.img} />
              <div className={styles.subDetails}>
                {post?.id && <span>{formatDigit(post?.views ?? 0)} views</span>}
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
            <div className={styles.column2}>
              <h3>You Might Also Like:</h3>
              <div className={styles.postCont}>
                {randomPostsData &&
                  randomPostsData.map((randomPost) => (
                    <Link
                      key={randomPost.id}
                      className={styles.card}
                      to={ROUTES.POST(randomPost.id, randomPost.title)}
                    >
                      <div className={styles.imageHolder}>
                        <img src={randomPost.postImgUrl} alt="post image" />
                      </div>
                      <div className={styles.textHolder}>
                        <h3>{randomPost.title}</h3>
                        <div className={styles.subText}>
                          <span>{formatDigit(randomPost.views)} views</span>
                          <span>{randomPost.createdAt}</span>
                        </div>
                      </div>
                    </Link>
                  ))}
              </div>
            </div>
          </div>
        </div>
      </LoadingScreen>
    </>
  );
};
