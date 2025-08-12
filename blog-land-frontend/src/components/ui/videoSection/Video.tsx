import React, { useState } from 'react';
import Dialog from '@mui/material/Dialog';
import styles from './Video.module.css';
import { Video } from '../../../types/video/video';

export const VideoSection = () => {
  const [activeVideo, setActiveVideo] = useState<string | null>(null);

  const videos: Video[] = [
    {
      videoId: '1',
      title: 'Ponpon Chen｜The World News Polka (ABC-TV)',
      url: 'videos/1.webm',
      category: 'Other',
    },
    { videoId: '2', title: 'Markets Updates SABC News', url: 'videos/2.webm', category: 'Finance' },
    {
      videoId: '3',
      title: 'Queen Elizabeth I Gun salute in honour of Britain',
      url: 'videos/3.webm',
      category: 'Politics',
    },
  ];

  const handleClose = () => {
    setActiveVideo(null);
  };

  return (
    <div className={styles.videoContainer}>
      <div className="container">
        <div className={styles.row1}>
          <h2>Video Highlights</h2>
        </div>

        <div className={styles.row2}>
          {videos.map((video) => (
            <div key={video.videoId} className={styles.video}>
              <div className={styles.videoWrapper}>
                <video className={styles.frame} src={video.url} preload="metadata" muted />
                <div className={styles.overlay}>
                  <button className={styles.playButton} onClick={() => setActiveVideo(video.url)}>
                    ▶
                  </button>
                </div>
              </div>
              <span className={styles.category}>{video.category}</span>
              <span className={styles.title}>{video.title}</span>
            </div>
          ))}
        </div>
      </div>

      <Dialog
        open={Boolean(activeVideo)}
        onClose={handleClose}
        maxWidth="md"
        fullWidth
        PaperProps={{
          style: { backgroundColor: 'transparent', boxShadow: 'none' },
        }}
      >
        {activeVideo && (
          <video src={activeVideo} controls autoPlay style={{ width: '100%', borderRadius: 16 }} />
        )}
      </Dialog>
    </div>
  );
};
