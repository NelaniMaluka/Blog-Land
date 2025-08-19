import * as React from 'react';
import {
  Avatar,
  Badge,
  Box,
  Divider,
  List,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Tooltip,
} from '@mui/material';
import AccountCircleIcon from '@mui/icons-material/AccountCircle';
import ArticleIcon from '@mui/icons-material/Article';
import LogoutIcon from '@mui/icons-material/Logout';
import styles from './SideMenu.module.css';
import { useNavigate } from 'react-router-dom';
import { ROUTES } from '../../../../constants/routes';
import LoadingScreen from '../../../../features/LoadingScreen/LoadingScreen';

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

  const handleLogout = () => {
    logout.mutateAsync();
    navigate(ROUTES.HOME);
  };

  return (
    <Box className={styles.container}>
      <div className={styles.headerContainer}>
        <LoadingScreen isLoading={isLoading}>
          <Box className={styles.header}>
            <Avatar className={styles.avatar}>NM</Avatar>
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
            <AccountCircleIcon className={styles.icon} />
          </ListItemIcon>
          <ListItemText primary="Profile" primaryTypographyProps={{ className: styles.text }} />
        </ListItemButton>

        <ListItemButton selected={selected === 'posts'} onClick={() => onSelect('posts')}>
          <ListItemIcon>
            <ArticleIcon className={styles.icon} />
          </ListItemIcon>
          <ListItemText primary="Posts" primaryTypographyProps={{ className: styles.text }} />
        </ListItemButton>
      </List>

      <Box className={styles.flexGrow} />

      <Divider />

      <Box className={styles.logout}>
        <ListItemButton onClick={handleLogout}>
          <ListItemIcon>
            <LogoutIcon className={styles.icon} />
          </ListItemIcon>
          <ListItemText primary="Log out" primaryTypographyProps={{ className: styles.text }} />
        </ListItemButton>
      </Box>
    </Box>
  );
};
