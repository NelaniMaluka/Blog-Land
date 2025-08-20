import { motion, MotionProps } from 'framer-motion';
import React, { useEffect, useRef, useState } from 'react';
import { Link } from 'react-router-dom';
import styles from './LatestCta.module.css';
import { useGetLatestPosts } from '../../../../hooks/usePost';
import { ROUTES } from '../../../../constants/routes';
import LoadingScreen from '../../../../features/LoadingScreen/LoadingScreen';

type MotionDivProps = React.HTMLAttributes<HTMLDivElement> & MotionProps;

export const MotionDiv: React.FC<MotionDivProps> = ({ children, ...props }) => {
  return <motion.div {...props}>{children}</motion.div>;
};

const shuffle = <T,>(array: T[]): T[] => {
  let currentIndex = array.length,
    randomIndex;

  while (currentIndex !== 0) {
    randomIndex = Math.floor(Math.random() * currentIndex);
    currentIndex--;

    [array[currentIndex], array[randomIndex]] = [array[randomIndex], array[currentIndex]];
  }

  return array;
};

const ShuffleGrid: React.FC = () => {
  const { data, isLoading, isError } = useGetLatestPosts({ page: 1, size: 16 });
  const timeoutRef = useRef<ReturnType<typeof setTimeout> | null>(null);
  const [squares, setSquares] = useState<any[]>([]);

  useEffect(() => {
    if (!data || data.length === 0) return;

    // Set initial shuffled data
    setSquares(shuffle([...data]));

    const shuffleSquares = () => {
      setSquares((prevSquares) => shuffle([...prevSquares]));
      timeoutRef.current = setTimeout(shuffleSquares, 3000);
    };

    // Start shuffling after initial set (with a short delay to ensure animation triggers)
    timeoutRef.current = setTimeout(shuffleSquares, 100);

    return () => {
      if (timeoutRef.current) clearTimeout(timeoutRef.current);
    };
  }, [data]);

  if (isError || !data || data.length === 0) return null;

  return (
    <LoadingScreen isLoading={isLoading}>
      <div className="grid grid-cols-4 grid-rows-4 h-[450px] gap-1 overflow-hidden">
        {squares.map((sq) => (
          <MotionDiv
            key={sq.id}
            layout
            transition={{ duration: 1.5, type: 'spring' }}
            className="w-full h-full aspect-square bg-center bg-cover"
            style={{ backgroundImage: `url(${sq.postImgUrl})`, backgroundSize: 'cover' }}
          />
        ))}
      </div>
    </LoadingScreen>
  );
};

const ShuffleHero: React.FC = () => {
  return (
    <section className="container">
      <div className={styles.section}>
        <div>
          <span className={styles.tagline}>Stay Informed</span>
          <h2 className={styles.heading}>Check Out Our Latest Blog Topics</h2>
          <p className={styles.text}>
            Blog Land is where ideas come alive. Browse our latest posts filled with tips, insights,
            and stories to keep you inspired and up-to-date.
          </p>
          <Link to={ROUTES.LATEST_POSTS} className={styles.button}>
            Read More Blogs
          </Link>
        </div>
        <ShuffleGrid />
      </div>
    </section>
  );
};

export default ShuffleHero;
