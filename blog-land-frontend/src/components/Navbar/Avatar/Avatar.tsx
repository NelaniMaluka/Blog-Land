import * as React from 'react';
import Avatar from '@mui/material/Avatar';
import IconButton from '@mui/material/IconButton';
import Menu from '@mui/material/Menu';
import MenuItem from '@mui/material/MenuItem';
import PersonAddIcon from '@mui/icons-material/PersonAdd';
import LoginIcon from '@mui/icons-material/Login';
import AccountCircleIcon from '@mui/icons-material/AccountCircle';
import ArticleIcon from '@mui/icons-material/Article';
import CommentIcon from '@mui/icons-material/Comment';
import { ROUTES } from '../../../constants/routes';

import RegisterDialog from '../../forms/Register';
import LoginDialog from '../../forms/Login';
import { useLogoutUser } from '../../../hooks/useUser';
import { store } from '../../../store/store';
import styles from './Avatar.module.css';
import { Routes } from 'react-router-dom';

export default function AvatarMenu() {
  const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
  const [openRegister, setOpenRegister] = React.useState(false);
  const [openLogin, setOpenLogin] = React.useState(false);
  const logout = useLogoutUser();

  const open = Boolean(anchorEl);

  const handleClick = (event: React.MouseEvent<HTMLElement>) => setAnchorEl(event.currentTarget);
  const handleCloseMenu = () => setAnchorEl(null);
  const handleLogout = () => {
    logout.mutate();
    handleCloseMenu();
  };

  const switchToRegister = () => {
    setOpenLogin(false);
    setOpenRegister(true);
  };

  const switchToLogin = () => {
    setOpenRegister(false);
    setOpenLogin(true);
  };

  const auth = store.getState().auth.isAuthenticated;

  return (
    <>
      <IconButton
        onClick={handleClick}
        size="small"
        aria-controls={open ? 'account-menu' : undefined}
        aria-haspopup="true"
        aria-expanded={open ? 'true' : undefined}
      >
        <Avatar src="/broken-image.jpg" className={styles.avatarButton} />
      </IconButton>

      <Menu
        anchorEl={anchorEl}
        id="account-menu"
        open={open}
        onClose={handleCloseMenu}
        PaperProps={{ elevation: 4, className: styles.menuPaper }}
        transformOrigin={{ horizontal: 'right', vertical: 'top' }}
        anchorOrigin={{ horizontal: 'right', vertical: 'bottom' }}
      >
        {auth
          ? [
              <a href={ROUTES.DASHBOARD}>
                <MenuItem key="profile" className={styles.menuItem}>
                  <AccountCircleIcon className={styles.menuIcon} /> Profile
                </MenuItem>
              </a>,
              <a href={ROUTES.DASHBOARD}>
                <MenuItem key="myposts" className={styles.menuItem}>
                  <ArticleIcon className={styles.menuIcon} /> My Posts
                </MenuItem>
              </a>,
              <a href={ROUTES.DASHBOARD}>
                <MenuItem key="mycomments" className={styles.menuItem}>
                  <CommentIcon className={styles.menuIcon} /> My Comments
                </MenuItem>
              </a>,
              <a href={ROUTES.DASHBOARD}>
                <MenuItem key="logout" onClick={handleLogout} className={styles.menuItem}>
                  <CommentIcon className={styles.menuIcon} /> Logout
                </MenuItem>
              </a>,
            ]
          : [
              <MenuItem
                key="register"
                onClick={() => {
                  setOpenRegister(true);
                  handleCloseMenu();
                }}
                className={styles.menuItem}
              >
                <PersonAddIcon className={styles.menuIcon} /> Register
              </MenuItem>,
              <MenuItem
                key="login"
                onClick={() => {
                  setOpenLogin(true);
                  handleCloseMenu();
                }}
                className={styles.menuItem}
              >
                <LoginIcon className={styles.menuIcon} /> Login
              </MenuItem>,
            ]}
      </Menu>

      <RegisterDialog
        open={openRegister}
        onClose={() => setOpenRegister(false)}
        onSwitchToLogin={switchToLogin}
      />
      <LoginDialog
        open={openLogin}
        onClose={() => setOpenLogin(false)}
        onSwitchToRegister={switchToRegister}
      />
    </>
  );
}
