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

export default function AvatarMenu() {
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const [openLoginDialog, setOpenLoginDialog] = useState(false);
  const [openRegisterDialog, setOpenRegisterDialog] = useState(false);
  const [openForgotDialog, setOpenForgotDialog] = useState(false);
  const logout = useLogoutUser();

  const open = Boolean(anchorEl);
  const auth = store.getState().auth.isAuthenticated;

  const handleClick = (event: React.MouseEvent<HTMLElement>) => setAnchorEl(event.currentTarget);
  const handleCloseMenu = () => setAnchorEl(null);

  const handleLogout = () => {
    logout.mutate();
    handleCloseMenu();
  };

  return (
    <>
      <IconButton onClick={handleClick} size="small">
        <Avatar src="/broken-image.jpg" className={styles.avatarButton} />
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
                  setOpenRegisterDialog(true);
                  handleCloseMenu();
                }}
                className={styles.menuItem}
              >
                <img className={styles.menuIcon} src="/icons/register.png" alt="register icon" />
                Register
              </MenuItem>,
              <MenuItem
                key="login"
                onClick={() => {
                  setOpenLoginDialog(true);
                  handleCloseMenu();
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
        open={openLoginDialog}
        onClose={() => setOpenLoginDialog(false)}
        onSwitchToRegister={() => {
          setOpenLoginDialog(false);
          setOpenRegisterDialog(true);
        }}
        onSwitchToForgot={() => {
          setOpenLoginDialog(false);
          setOpenForgotDialog(true);
        }}
      />

      {/* Register */}
      <RegisterDialog
        open={openRegisterDialog}
        onClose={() => setOpenRegisterDialog(false)}
        onSwitchToLogin={() => {
          setOpenRegisterDialog(false);
          setOpenLoginDialog(true);
        }}
      />

      {/* Forgot Password */}
      <ForgotPasswordDialog
        open={openForgotDialog}
        onClose={() => setOpenForgotDialog(false)}
        onSwitchToLogin={() => {
          setOpenForgotDialog(false);
          setOpenLoginDialog(true);
        }}
      />
    </>
  );
}
