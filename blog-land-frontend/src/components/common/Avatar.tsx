import * as React from 'react';
import Avatar from '@mui/material/Avatar';
import { UserResponse } from '../../types/user/response';
import { useLocation } from 'react-router-dom';

interface FallbackAvatarsProps {
  user: UserResponse;
  isProfile?: boolean;
  onFileSelect?: (file: File | null) => void; // 👈 notify parent
}

export default function FallbackAvatars({ user, isProfile, onFileSelect }: FallbackAvatarsProps) {
  const location = useLocation();
  const isProfilePage = location.pathname === '/dashboard/profile';

  const [avatarUrl, setAvatarUrl] = React.useState(user.profileIconUrl || '/broken-image.jpg');
  const fileInputRef = React.useRef<HTMLInputElement>(null);

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
    }
  };

  return (
    <div style={{ display: 'inline-block', cursor: isProfilePage ? 'pointer' : 'default' }}>
      <Avatar
        src={avatarUrl}
        sx={{ bgcolor: 'darkgrey', fontSize: '0.7rem' }}
        alt={user.firstname + ' ' + user.lastname}
        onClick={handleAvatarClick}
      />
      <input
        ref={fileInputRef}
        type="file"
        accept="image/*"
        style={{ display: 'none' }}
        onChange={handleFileChange}
      />
    </div>
  );
}
