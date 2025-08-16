import React, { useState, useRef } from 'react';
import Dialog from '@mui/material/Dialog';
import IconButton from '@mui/material/IconButton';
import { ChevronLeft, ChevronRight } from '@mui/icons-material';
import styles from './Video.module.css';
import { useGetYoutubeVideos } from '../../../hooks/useTechCrunch';
import LoadingScreen from '../../../features/LoadingScreen/LoadingScreen';

export const VideoSection = () => {
  const [activeVideo, setActiveVideo] = useState<string | null>(null);
  const { data: videos = [], isLoading, isError } = useGetYoutubeVideos();
  const scrollRef = useRef<HTMLDivElement>(null);

  if (isError || videos.length <= 0) return <></>;

  const scroll = (direction: 'left' | 'right') => {
    if (scrollRef.current) {
      const scrollAmount = scrollRef.current.clientWidth;
      scrollRef.current.scrollBy({
        left: direction === 'left' ? -scrollAmount : scrollAmount,
        behavior: 'smooth',
      });
    }
  };

  return (
    <LoadingScreen isLoading={isLoading}>
      <div className={styles.videoContainer}>
        <div className="container">
          <div className={styles.row1}>
            <h2>Top News Videos</h2>

            {/* Desktop-only arrows */}
            <div className={styles.desktopArrows}>
              <IconButton onClick={() => scroll('left')}>
                <ChevronLeft />
              </IconButton>
              <IconButton onClick={() => scroll('right')}>
                <ChevronRight />
              </IconButton>
            </div>
          </div>

          <div className={styles.scrollWrapper}>
            {/* Scrollable row */}
            <div className={styles.row2} ref={scrollRef}>
              {videos.slice(0, 10).map((video) => (
                <div key={video.id.videoId} className={styles.video}>
                  <div className={styles.videoWrapper}>
                    <div
                      className={styles.thumbnail}
                      style={{
                        backgroundImage: `url(https://img.youtube.com/vi/${video.id.videoId}/hqdefault.jpg)`,
                      }}
                    >
                      <div
                        className={styles.overlay}
                        onClick={() => setActiveVideo(video.id.videoId)}
                      >
                        ▶
                      </div>
                    </div>
                  </div>
                  <span className={styles.category}>News</span>
                  <span className={styles.title}>{video.snippet.title}</span>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Popup dialog */}
        <Dialog
          open={Boolean(activeVideo)}
          onClose={() => setActiveVideo(null)}
          maxWidth="md"
          fullWidth
          PaperProps={{
            style: { backgroundColor: 'transparent', boxShadow: 'none' },
          }}
        >
          {activeVideo && (
            <iframe
              width="100%"
              height="500"
              src={`https://www.youtube.com/embed/${activeVideo}?autoplay=1`}
              frameBorder="0"
              allow="autoplay; encrypted-media"
              allowFullScreen
              style={{ borderRadius: 16 }}
            />
          )}
        </Dialog>
      </div>
    </LoadingScreen>
  );
};
