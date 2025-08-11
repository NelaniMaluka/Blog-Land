import * as React from 'react';
import Button from '@mui/material/Button';
import GoogleMultiColorIcon from './GoogleMultiColorIcon'; // adjust path as needed

interface GoogleOAuthButtonProps {
  onClick?: () => void;
  redirectUrl?: string;
}

export default function GoogleOAuthButton({ onClick, redirectUrl }: GoogleOAuthButtonProps) {
  const handleClick = () => {
    if (onClick) onClick();
    if (redirectUrl) {
      window.location.href = redirectUrl; // redirect to Google OAuth
    }
  };

  return (
    <Button
      variant="contained"
      startIcon={<GoogleMultiColorIcon />}
      fullWidth
      sx={{
        width: '90%',
        margin: 'auto',
        borderRadius: '10px',
        textTransform: 'none',
        fontSize: '0.45rem',
        fontWeight: 500,
        color: 'white',
        boxShadow: '0 2px 5px rgba(0,0,0,0.3)',
        backgroundColor: '#313131ff',
        '&:hover': {
          backgroundColor: '#202020ff',
          boxShadow: '0 4px 10px rgba(0,0,0,0.5)',
        },
      }}
      onClick={handleClick}
    >
      Sign in with Google
    </Button>
  );
}
