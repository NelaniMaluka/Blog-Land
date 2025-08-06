import * as React from 'react';
import Avatar from '@mui/material/Avatar';
import IconButton from '@mui/material/IconButton';
import Menu from '@mui/material/Menu';
import MenuItem from '@mui/material/MenuItem';
import PersonAddIcon from '@mui/icons-material/PersonAdd';
import LoginIcon from '@mui/icons-material/Login';
import RegisterDialog from '../LogIn/Register';
import LoginDialog from '../LogIn/Login';

export default function AvatarMenu() {
  const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
  const [openRegister, setOpenRegister] = React.useState(false);
  const [openLogin, setOpenLogin] = React.useState(false);

  const open = Boolean(anchorEl);

  const handleClick = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleCloseMenu = () => {
    setAnchorEl(null);
  };

  return (
    <>
      <IconButton
        onClick={handleClick}
        size="small"
        aria-controls={open ? 'account-menu' : undefined}
        aria-haspopup="true"
        aria-expanded={open ? 'true' : undefined}
      >
        <Avatar src="/broken-image.jpg" sx={{ width: 40, height: 40 }} />
      </IconButton>
      <Menu
        anchorEl={anchorEl}
        id="account-menu"
        open={open}
        onClose={handleCloseMenu}
        PaperProps={{
          elevation: 4,
          sx: { mt: 1, minWidth: 150 },
        }}
        transformOrigin={{ horizontal: 'right', vertical: 'top' }}
        anchorOrigin={{ horizontal: 'right', vertical: 'bottom' }}
      >
        <MenuItem
          onClick={() => {
            setOpenRegister(true);
            handleCloseMenu();
          }}
          sx={{ display: 'flex', alignItems: 'center', fontSize: '0.6rem', gap: 1 }}
        >
          <PersonAddIcon sx={{ fontSize: '0.8rem' }} />
          Register
        </MenuItem>

        <MenuItem
          onClick={() => {
            setOpenLogin(true);
            handleCloseMenu();
          }}
          sx={{ display: 'flex', alignItems: 'center', fontSize: '0.6rem', gap: 1 }}
        >
          <LoginIcon sx={{ fontSize: '0.8rem' }} />
          Login
        </MenuItem>
      </Menu>

      {/* Popups */}
      <RegisterDialog open={openRegister} onClose={() => setOpenRegister(false)} />
      <LoginDialog open={openLogin} onClose={() => setOpenLogin(false)} />
    </>
  );
}
