import * as React from 'react';
import Avatar from '@mui/material/Avatar';
import IconButton from '@mui/material/IconButton';
import Menu from '@mui/material/Menu';
import MenuItem from '@mui/material/MenuItem';
import { useState } from 'react';
import { ROUTES } from '../../../constants/routes';
import { useLogoutUser } from '../../../hooks/useUser';
import { store } from '../../../store/store';
import styles from './Avatar.module.css';
import LoginDialog from '../../forms/Login';
import RegisterDialog from '../../forms/Register';
import ForgotPasswordDialog from '../../forms/ForgotPassword';
import { useQueryClient } from '@tanstack/react-query';
import { useDialog } from '../../../features/LoginProvider/LoginProvider';
import FallbackAvatars from '../../common/Avatar';
import { useSelector } from 'react-redux';
import { RootState } from '../../../store/store';

export default function AvatarMenu() {
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const logout = useLogoutUser();
  const queryClient = useQueryClient();

  const { openLogin, openRegister, openForgot, hideAll, showLogin, showRegister, showForgot } =
    useDialog();

  const open = Boolean(anchorEl);
  const auth = useSelector((state: RootState) => state.auth.isAuthenticated);
  const user = useSelector((state: RootState) => state.auth.user);

  const handleClick = (event: React.MouseEvent<HTMLElement>) => setAnchorEl(event.currentTarget);
  const handleCloseMenu = () => setAnchorEl(null);

  const handleLogout = () => {
    logout.mutate();
    handleCloseMenu();
    queryClient.clear();
  };

  return (
    <>
      <IconButton onClick={handleClick} size="small">
        {auth ? (
          <FallbackAvatars user={user} />
        ) : (
          <Avatar src="/broken-image.jpg" className={styles.avatarButton} />
        )}
      </IconButton>

      <Menu
        anchorEl={anchorEl}
        open={open}
        onClose={handleCloseMenu}
        PaperProps={{ elevation: 4, className: styles.menuPaper }}
        transformOrigin={{ horizontal: 'right', vertical: 'top' }}
        anchorOrigin={{ horizontal: 'right', vertical: 'bottom' }}
        disableScrollLock
      >
        {auth
          ? [
              <MenuItem
                key="profile"
                component="a"
                href={ROUTES.DASHBOARD_PROFILE}
                className={styles.menuItem}
              >
                <img className={styles.menuIcon} src="/icons/profile.png" alt="profile icon" />
                Profile
              </MenuItem>,
              <MenuItem
                key="blogs"
                component="a"
                href={ROUTES.DASHBOARD_POSTS}
                className={styles.menuItem}
              >
                <img className={styles.menuIcon} src="/icons/blog.png" alt="blog icon" />
                My Blogs
              </MenuItem>,
              <MenuItem key="logout" onClick={handleLogout} className={styles.menuItem}>
                <img className={styles.menuIcon} src="/icons/logout.png" alt="logout icon" />
                Logout
              </MenuItem>,
            ]
          : [
              <MenuItem
                key="register"
                onClick={() => {
                  handleCloseMenu();
                  showRegister();
                }}
                className={styles.menuItem}
              >
                <img className={styles.menuIcon} src="/icons/register.png" alt="register icon" />
                Register
              </MenuItem>,

              <MenuItem
                key="login"
                onClick={() => {
                  handleCloseMenu();
                  showLogin();
                }}
                className={styles.menuItem}
              >
                <img className={styles.menuIcon} src="/icons/login.png" alt="login icon" />
                Login
              </MenuItem>,
            ]}
      </Menu>

      {/* Login */}
      <LoginDialog
        open={openLogin}
        onClose={hideAll}
        onSwitchToRegister={() => {
          hideAll();
          setTimeout(showRegister, 100);
        }}
        onSwitchToForgot={() => {
          hideAll();
          setTimeout(showForgot, 100);
        }}
      />

      {/* Register */}
      <RegisterDialog
        open={openRegister}
        onClose={hideAll}
        onSwitchToLogin={() => {
          hideAll();
          setTimeout(showLogin, 100);
        }}
      />

      {/* Forgot Password */}
      <ForgotPasswordDialog
        open={openForgot}
        onClose={hideAll}
        onSwitchToLogin={() => {
          hideAll();
          setTimeout(showLogin, 100);
        }}
      />
    </>
  );
}
