// AppBarDashboard.tsx
import * as React from 'react';
import { AppBar, Avatar, Box, IconButton, Toolbar, Typography, useMediaQuery } from '@mui/material';
import { useTheme } from '@mui/material/styles';
import MenuIcon from '@mui/icons-material/Menu';
import { ROUTES } from '../../../../constants/routes';

interface AppBarDashboardProps {
  onMenuClick: () => void;
}

export const AppBarDashboard: React.FC<AppBarDashboardProps> = ({ onMenuClick }) => {
  const theme = useTheme();
  const upMd = useMediaQuery(theme.breakpoints.up('md'));

  return (
    <AppBar
      color="inherit"
      elevation={0}
      position="fixed"
      sx={{ borderBottom: 1, borderColor: 'divider', zIndex: (t) => t.zIndex.drawer + 1 }}
    >
      <Toolbar>
        {!upMd && (
          <IconButton
            color="inherit"
            edge="start"
            onClick={onMenuClick}
            sx={{ mr: 1 }}
            aria-label="open drawer"
          >
            <MenuIcon />
          </IconButton>
        )}
        <Typography variant="h6" fontWeight={700} noWrap>
          <a href={ROUTES.DASHBOARD}>
            <h3>Blog-Land</h3>
          </a>
        </Typography>
        <Box sx={{ flexGrow: 1 }} />
        <Avatar sx={{ width: 32, height: 32 }}>N</Avatar>
      </Toolbar>
    </AppBar>
  );
};
