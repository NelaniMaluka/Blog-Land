// AvatarMenu.tsx
import * as React from 'react';
import Avatar from '@mui/material/Avatar';
import IconButton from '@mui/material/IconButton';
import Menu from '@mui/material/Menu';
import MenuItem from '@mui/material/MenuItem';
import { useEffect, useState } from 'react';
import { ROUTES } from '../../../constants/routes';
import { useLogoutUser } from '../../../hooks/useUser';
import { store } from '../../../store/store';
import styles from './Avatar.module.css';
import LoginDialog from '../../forms/Login';
import RegisterDialog from '../../forms/Register';

export default function AvatarMenu() {
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const [openLoginDialog, setOpenLoginDialog] = useState(false);
  const [openRegisterDialog, setOpenRegisterDialog] = useState(false);
  const logout = useLogoutUser();

  const open = Boolean(anchorEl);
  const auth = store.getState().auth.isAuthenticated;

  const handleClick = (event: React.MouseEvent<HTMLElement>) => setAnchorEl(event.currentTarget);
  const handleCloseMenu = () => setAnchorEl(null);

  const handleLogout = () => {
    logout.mutate();
    handleCloseMenu();
  };

  useEffect(() => {
    if (!anchorEl) return;

    const handleScrollOrClick = (event: Event) => {
      const target = event.target as HTMLElement;
      if (anchorEl.contains(target) || document.getElementById('account-menu')?.contains(target)) {
        return;
      }
      setAnchorEl(null);
    };

    window.addEventListener('scroll', handleScrollOrClick, true);
    window.addEventListener('click', handleScrollOrClick);

    return () => {
      window.removeEventListener('scroll', handleScrollOrClick, true);
      window.removeEventListener('click', handleScrollOrClick);
    };
  }, [anchorEl]);

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
        disableScrollLock={true}
      >
        {auth
          ? [
              <a key="profile" href={ROUTES.DASHBOARD_PROFILE}>
                <MenuItem className={styles.menuItem}>
                  <img className={styles.menuIcon} src="/icons/profile.png" alt="profile icon" />
                  Profile
                </MenuItem>
              </a>,
              <a key="posts" href={ROUTES.DASHBOARD_POSTS}>
                <MenuItem className={styles.menuItem}>
                  <img className={styles.menuIcon} src="/icons/blog.png" alt="blog icon" />
                  My Posts
                </MenuItem>
              </a>,
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

      {/* Login Dialog */}
      <LoginDialog
        open={openLoginDialog}
        onClose={() => setOpenLoginDialog(false)}
        onSwitchToRegister={() => {
          setOpenLoginDialog(false);
          setOpenRegisterDialog(true);
        }}
      />

      {/* Register Dialog */}
      <RegisterDialog
        open={openRegisterDialog}
        onClose={() => setOpenRegisterDialog(false)}
        onSwitchToLogin={() => {
          setOpenRegisterDialog(false);
          setOpenLoginDialog(true);
        }}
      />
    </>
  );
}
