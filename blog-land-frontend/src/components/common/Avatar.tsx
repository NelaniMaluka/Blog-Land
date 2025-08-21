import * as React from 'react';
import Avatar from '@mui/material/Avatar';
import { deepOrange, grey } from '@mui/material/colors';
import { UserResponse } from '../../types/user/response';

interface FallbackAvatarsProps {
  user: UserResponse;
}

export default function FallbackAvatars({ user }: FallbackAvatarsProps) {
  if (user.profileIconUrl) {
    return <Avatar src={user.profileIconUrl} />;
  } else {
    return (
      <Avatar
        sx={{ bgcolor: 'darkgrey', fontSize: '70%' }}
        alt={user.firstname + ' ' + user.lastname}
        src="/broken-image.jpg"
      />
    );
  }
}
