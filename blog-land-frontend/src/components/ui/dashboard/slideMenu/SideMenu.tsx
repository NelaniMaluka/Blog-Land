import * as React from 'react';
import {
  Avatar,
  Box,
  Divider,
  List,
  ListItemButton,
  ListItemIcon,
  ListItemText,
} from '@mui/material';
import styles from './SideMenu.module.css';
import { useNavigate } from 'react-router-dom';
import { ROUTES } from '../../../../constants/routes';
import LoadingScreen from '../../../../features/LoadingScreen/LoadingScreen';
import FallbackAvatars from '../../../common/Avatar';
import { useQueryClient } from '@tanstack/react-query';

import { useGetUser, useLogoutUser } from '../../../../hooks/useUser';

export type MenuKey = 'profile' | 'posts';

interface SidebarMenuProps {
  selected: MenuKey;
  onSelect: (key: MenuKey) => void;
  onLogout?: () => void;
}

export const SidebarMenu: React.FC<SidebarMenuProps> = ({ selected, onSelect, onLogout }) => {
  const { data: user, isLoading, isError } = useGetUser();
  const navigate = useNavigate();
  const logout = useLogoutUser();
  const queryClient = useQueryClient();

  const handleLogout = () => {
    logout.mutateAsync();
    navigate(ROUTES.HOME);
    queryClient.clear();
  };

  return (
    <Box className={styles.container}>
      <a href={ROUTES.HOME}>
        <h3 className={styles.logo}>Blog-Land</h3>
      </a>
      <Divider />
      <div className={styles.headerContainer}>
        <LoadingScreen isLoading={isLoading}>
          <Box className={styles.header}>
            <Box className={styles.header}>
              {user ? (
                <FallbackAvatars user={user} />
              ) : (
                <Avatar sx={{ bgcolor: 'darkgrey', fontSize: '70%' }}>?</Avatar>
              )}
            </Box>

            <Box className={styles.userInfo}>
              <Box className={styles.userName}>{user?.firstname + ' ' + user?.lastname}</Box>
              <Box className={styles.userEmail}>{user?.email}</Box>
            </Box>
          </Box>
        </LoadingScreen>
      </div>

      <Divider />

      <List dense disablePadding>
        <ListItemButton selected={selected === 'profile'} onClick={() => onSelect('profile')}>
          <ListItemIcon>
            <img src="/icons/profile.png" alt="profile-icon" className={styles.icon} />
          </ListItemIcon>
          <ListItemText primary="Profile" primaryTypographyProps={{ className: styles.text }} />
        </ListItemButton>

        <ListItemButton selected={selected === 'posts'} onClick={() => onSelect('posts')}>
          <ListItemIcon>
            <img src="/icons/blog.png" alt="blog-icon" className={styles.icon} />
          </ListItemIcon>
          <ListItemText primary="Posts" primaryTypographyProps={{ className: styles.text }} />
        </ListItemButton>
      </List>

      <Box className={styles.flexGrow} />

      <Divider />

      <Box className={styles.logout}>
        <ListItemButton onClick={handleLogout}>
          <ListItemIcon>
            <img src="/icons/logout.png" alt="logout-icon" className={styles.icon} />
          </ListItemIcon>
          <ListItemText primary="Log out" primaryTypographyProps={{ className: styles.text }} />
        </ListItemButton>
      </Box>
    </Box>
  );
};
