import React, { ReactNode } from 'react';
import Lottie from 'lottie-react';
import loadingAnimation from './loadingAnimation.json';
import styles from './LoadingScreen.module.css';

interface LoadingScreenProps {
  isLoading: boolean;
  children: ReactNode;
}

export default function LoadingScreen({ isLoading, children }: LoadingScreenProps) {
  return (
    <>
      {isLoading && (
        <div className={styles.overlay}>
          <Lottie
            animationData={loadingAnimation}
            loop={true}
            style={{ width: 150, height: 150 }}
          />
        </div>
      )}
      <div className={isLoading ? styles.blurBackground : styles.normalBackground}>{children}</div>
    </>
  );
}
