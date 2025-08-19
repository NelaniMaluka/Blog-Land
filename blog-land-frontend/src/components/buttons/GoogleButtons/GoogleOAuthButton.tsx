import * as React from 'react';
import Button from '@mui/material/Button';
import GoogleMultiColorIcon from './GoogleMultiColorIcon';
import styles from './GoogleOAuthButton.module.css';

interface GoogleOAuthButtonProps {
  onClick?: () => void;
  redirectUrl?: string;
}

export default function GoogleOAuthButton({ onClick, redirectUrl }: GoogleOAuthButtonProps) {
  const handleClick = () => {
    if (onClick) onClick();
    if (redirectUrl) {
      window.location.href = redirectUrl;
    }
  };

  return (
    <Button
      variant="contained"
      startIcon={<GoogleMultiColorIcon />}
      fullWidth
      className={styles.button}
      onClick={handleClick}
    >
      Sign in with Google
    </Button>
  );
}
