import * as React from 'react';
import Avatar from '@mui/material/Avatar';
import EditIcon from '@mui/icons-material/Edit';
import { Box } from '@mui/material';
import { UserResponse } from '../../types/user/response';
import { useLocation } from 'react-router-dom';
import { useEffect } from 'react';
import styles from './Avatar.module.css';
import DeleteIcon from '@mui/icons-material/Delete';

interface FallbackAvatarsProps {
  user: UserResponse;
  isProfile?: boolean;
  onFileSelect?: (file: File | 'Remove' | null) => void;
}

export default function FallbackAvatars({ user, isProfile, onFileSelect }: FallbackAvatarsProps) {
  const location = useLocation();
  const isProfilePage = location.pathname === '/dashboard/profile';

  const [avatarUrl, setAvatarUrl] = React.useState(user.profileIconUrl || '/broken-image.jpg');
  const fileInputRef = React.useRef<HTMLInputElement>(null);

  useEffect(() => {
    setAvatarUrl(
      user.profileIconUrl ? `${user.profileIconUrl}?t=${Date.now()}` : '/broken-image.jpg'
    );
  }, [user.profileIconUrl]);

  const handleAvatarClick = () => {
    if (isProfilePage && fileInputRef.current && isProfile) {
      fileInputRef.current.click();
    }
  };

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.files && event.target.files[0]) {
      const file = event.target.files[0];
      setAvatarUrl(URL.createObjectURL(file));
      onFileSelect?.(file);
      event.target.value = '';
    }
  };

  const handleRemoveAvatar = () => {
    setAvatarUrl('/broken-image.jpg');
    onFileSelect?.('Remove');
  };

  return (
    <Box className={styles.avatarContainer} onClick={handleAvatarClick}>
      <Avatar
        src={avatarUrl}
        alt={`${user.firstname} ${user.lastname}`}
        className={styles.avatar}
      />
      {isProfilePage && isProfile && (
        <>
          <Box className={styles.editIcon}>
            <EditIcon sx={{ fontSize: 16, color: '#fff' }} />
          </Box>
          {avatarUrl !== '/broken-image.jpg' && (
            <Box
              className={styles.removeIcon}
              onClick={(e) => {
                e.stopPropagation(); // prevent triggering file input
                handleRemoveAvatar();
              }}
            >
              <DeleteIcon sx={{ fontSize: 16, color: '#fff' }} />
            </Box>
          )}
        </>
      )}
      <input
        ref={fileInputRef}
        type="file"
        accept="image/*"
        style={{ display: 'none' }}
        onChange={handleFileChange}
        aria-label="Upload profile image"
      />
    </Box>
  );
}
