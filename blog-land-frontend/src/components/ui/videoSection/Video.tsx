import React, { useState, useRef } from 'react';
import Dialog from '@mui/material/Dialog';
import IconButton from '@mui/material/IconButton';
import { ChevronLeft, ChevronRight } from '@mui/icons-material';
import styles from './Video.module.css';
import LoadingScreen from '../../../features/LoadingScreen/LoadingScreen';

interface LocalVideo {
  id: string;
  title: string;
  src: string;
}

const localVideos: LocalVideo[] = [
  { id: 'video1', title: 'Ponpon Chen｜The World News Polka (ABC-TV)', src: '/videos/1.webm' },
  { id: 'video2', title: 'Markets Updates SABC News', src: '/videos/2.webm' },
  {
    id: 'video3',
    title: 'Queen Elizabeth I Gun salute in honour of Britain',
    src: '/videos/3.webm',
  },
];

export const VideoSection = () => {
  const [activeVideo, setActiveVideo] = useState<string | null>(null);
  const scrollRef = useRef<HTMLDivElement>(null);

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
    <LoadingScreen isLoading={false}>
      <div className={styles.videoContainer}>
        <div className="container">
          <div className={styles.row1}>
            <h2>Top News Videos</h2>

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
            <div className={styles.row2} ref={scrollRef}>
              {localVideos.map((video) => (
                <div key={video.id} className={styles.video}>
                  <div className={styles.videoWrapper}>
                    <video className={styles.thumbnail} src={video.src} muted controls={false} />
                    {/* Overlay Play Button */}
                    <div className={styles.overlay} onClick={() => setActiveVideo(video.src)}>
                      ▶
                    </div>
                  </div>
                  <span className={styles.category}>News</span>
                  <span className={styles.title}>{video.title}</span>
                </div>
              ))}
            </div>
          </div>
        </div>

        <Dialog
          open={Boolean(activeVideo)}
          onClose={() => setActiveVideo(null)}
          maxWidth="md"
          fullWidth
          PaperProps={{ style: { backgroundColor: 'transparent', boxShadow: 'none' } }}
        >
          {activeVideo && (
            <video width="100%" height="500" controls autoPlay style={{ borderRadius: 16 }}>
              <source src={activeVideo} type="video/webm" />
              Your browser does not support the video tag.
            </video>
          )}
        </Dialog>
      </div>
    </LoadingScreen>
  );
};
