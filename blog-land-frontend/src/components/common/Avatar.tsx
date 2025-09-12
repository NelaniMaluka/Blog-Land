import * as React from 'react';
import Avatar from '@mui/material/Avatar';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import { Box } from '@mui/material';
import { UserResponse } from '../../types/user/response';
import { useLocation } from 'react-router-dom';
import styles from './Avatar.module.css';
import { useEffect } from 'react';
interface FallbackAvatarsProps {
  user: UserResponse | null;
  isProfile?: boolean;
  onFileSelect?: (file: File | 'Remove' | null) => void;
}

export default function FallbackAvatars({ user, isProfile, onFileSelect }: FallbackAvatarsProps) {
  const location = useLocation();
  const isProfilePage = location.pathname === '/dashboard/profile';
  const [avatarUrl, setAvatarUrl] = React.useState<string | null>(user?.profileIconUrl || null);
  const fileInputRef = React.useRef<HTMLInputElement>(null);

  {
    /* Check if the profile icon is present */
  }
  useEffect(() => {
    if (user?.profileIconUrl) {
      setAvatarUrl(`${user.profileIconUrl}?t=${Date.now()}`);
    } else {
      setAvatarUrl(null);
    }
  }, [user?.profileIconUrl]);

  {
    /* initial format */
  }
  const initials =
    user?.firstname && user?.lastname
      ? `${user.firstname[0]}${user.lastname[0]}`.toUpperCase()
      : user?.firstname
      ? user.firstname[0].toUpperCase()
      : '?';

  {
    /* show upload bar */
  }
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
    setAvatarUrl(null);
    onFileSelect?.('Remove');
  };

  return (
    <Box className={styles.avatarContainer} onClick={handleAvatarClick}>
      <Avatar
        src={avatarUrl || undefined}
        alt={`${user?.firstname} ${user?.lastname}`}
        className={styles.avatar}
      >
        {!avatarUrl && initials}
      </Avatar>

      {isProfilePage && isProfile && (
        <>
          <Box className={styles.editIcon}>
            <EditIcon sx={{ fontSize: 16, color: '#fff' }} />
          </Box>
          {avatarUrl && (
            <Box
              className={styles.removeIcon}
              onClick={(e) => {
                e.stopPropagation();
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
