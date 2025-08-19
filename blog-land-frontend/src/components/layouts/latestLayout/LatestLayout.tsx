import { useGetLatestPosts } from '../../../hooks/usePost';
import LoadingScreen from '../../../features/LoadingScreen/LoadingScreen';
import * as React from 'react';
import Box from '@mui/material/Box';
import Masonry from '@mui/lab/Masonry';
import he from 'he';
import BasicBreadcrumbs from '../../breadcrumbs/breadcrumbs';
import { ROUTES } from '../../../constants/routes';

import styles from './LatestLayout.module.css';

const heights = [45, 33, 39, 37, 41, 35, 43, 38, 35, 39, 40, 35, 43, 35, 48, 42, 36, 44, 39, 41];
const MOBILE_BREAKPOINT = 600;
const MOBILE_HEIGHT_VH = 30;

export const LatestLayout = () => {
  const { data: latestPosts, isLoading } = useGetLatestPosts({ page: 1, size: 20 });
  const [windowWidth, setWindowWidth] = React.useState(window.innerWidth);

  React.useEffect(() => {
    const handleResize = () => setWindowWidth(window.innerWidth);
    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  const isMobile = windowWidth < MOBILE_BREAKPOINT;

  return (
    <LoadingScreen isLoading={isLoading}>
      <div className="container">
        <BasicBreadcrumbs title="Post" link={ROUTES.VIEW_ALL} page={'Latest'} />
        <Box className={styles.containerBox}>
          <Masonry
            spacing={2}
            columns={{
              xs: 1,
              sm: 2,
              md: 3,
              lg: 4,
            }}
          >
            {(latestPosts ?? []).map((post, index) => {
              const heightVh = isMobile ? MOBILE_HEIGHT_VH : heights[index % heights.length];
              console.log(post);
              return (
                <div
                  key={post.id || index}
                  className={styles.item}
                  style={{ '--item-height': `${heightVh}vh` } as React.CSSProperties}
                >
                  <a href={ROUTES.LATEST_POST_Page(post.title)}>
                    <img src={post.postImgUrl} alt={post.title} />
                    <div className={styles.overlay}>
                      <p>{he.decode(post.title)}</p>
                    </div>
                  </a>
                </div>
              );
            })}
          </Masonry>
        </Box>
      </div>
    </LoadingScreen>
  );
};
