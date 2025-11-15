import { useEffect, useState } from 'react';
import { useGetLatestPosts } from '../../../hooks/usePost';
import Masonry from '@mui/lab/Masonry';
import Box from '@mui/material/Box';
import he from 'he';
import { Link } from 'react-router-dom';
import LoadingScreen from '../../../features/LoadingScreen/LoadingScreen';
import BasicBreadcrumbs from '../../breadcrumbs/breadcrumbs';
import { ROUTES } from '../../../constants/routes';
import styles from './LatestLayout.module.css';

const MAX_PAGE = 3;

const heights = [
  45, 33, 39, 37, 41, 35, 43, 38, 35, 39, 40, 35, 43, 35, 48, 42, 36, 44, 39, 41, 45, 33, 39, 37,
  41, 35, 43, 38, 35, 39, 40, 35, 43, 35, 48, 42, 36, 44, 39, 41,
];

const MOBILE_BREAKPOINT = 600;
const MOBILE_HEIGHT_VH = 30;

export const LatestLayout = () => {
  const [page, setPage] = useState(1);
  const [posts, setPosts] = useState<any[]>([]);
  const [isEnd, setIsEnd] = useState(false);
  const [windowWidth, setWindowWidth] = useState(window.innerWidth);

  const isMobile = windowWidth < MOBILE_BREAKPOINT;

  const { data, isLoading, isError } = useGetLatestPosts({
    page,
    size: 20,
    enabled: page <= MAX_PAGE,
  });

  // Append posts when new page loads
  useEffect(() => {
    if (data) {
      setPosts((prev) => [...prev, ...data]);
      if (page >= MAX_PAGE) setIsEnd(true);
    }
  }, [data]);

  // Infinite scroll listener
  useEffect(() => {
    const handleScroll = () => {
      const nearBottom = window.innerHeight + window.scrollY >= document.body.offsetHeight - 300;

      if (nearBottom && !isLoading && !isEnd) {
        setPage((prev) => prev + 1);
      }
    };

    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, [isLoading, isEnd]);

  // Window resize listener (for mobile heights)
  useEffect(() => {
    const handleResize = () => setWindowWidth(window.innerWidth);
    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  if (isError) {
    return (
      <div className="container">
        <BasicBreadcrumbs page1={'Latest'} />
        <div className={styles.message}>Could not load data.</div>
      </div>
    );
  }

  return (
    <LoadingScreen isLoading={isLoading && posts.length === 0}>
      <section className="container">
        <BasicBreadcrumbs page1={'Latest'} />

        <Box className={styles.containerBox}>
          <Masonry spacing={2} columns={{ xs: 1, sm: 2, md: 3, lg: 4 }}>
            {posts.map((post, index) => {
              const heightVh = isMobile ? MOBILE_HEIGHT_VH : heights[index % heights.length];

              return (
                <div
                  key={post.id || index}
                  className={styles.item}
                  style={{ '--item-height': `${heightVh}vh` } as React.CSSProperties}
                >
                  <Link to={ROUTES.LATEST_POST_PAGE(post.title)} state={{ post }}>
                    <img src={post.postImgUrl} alt={post.title} />
                    <div className={styles.overlay}>
                      <p>{he.decode(post.title)}</p>
                    </div>
                  </Link>
                </div>
              );
            })}
          </Masonry>

          {/* Loader at bottom */}
          {isLoading && <div className={styles.loadingMore}>Loading more...</div>}
        </Box>
      </section>
    </LoadingScreen>
  );
};
