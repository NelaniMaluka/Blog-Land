import * as React from 'react';
import { Box, CssBaseline, Drawer, IconButton, useMediaQuery } from '@mui/material';
import { useTheme } from '@mui/material/styles';
import MenuIcon from '@mui/icons-material/Menu';
import { SidebarMenu, MenuKey } from './slideMenu/SideMenu';

const drawerWidth = 280;

interface DashboardProps {
  renderContent?: (selected: MenuKey) => React.ReactNode;
}

export const Dashboard: React.FC<DashboardProps> = ({ renderContent }) => {
  const theme = useTheme();
  const upMd = useMediaQuery(theme.breakpoints.up('md'));
  const [mobileOpen, setMobileOpen] = React.useState(false);
  const [selected, setSelected] = React.useState<MenuKey>('profile');

  const handleDrawerToggle = () => setMobileOpen(!mobileOpen);

  return (
    <Box sx={{ display: 'flex' }}>
      <CssBaseline />

      {/* Sidebar for desktop */}
      <Box component="nav" sx={{ width: { md: drawerWidth }, flexShrink: { md: 0 } }}>
        <Drawer
          variant="permanent"
          open
          sx={{
            display: { xs: 'none', md: 'block' },
            '& .MuiDrawer-paper': { width: drawerWidth, boxSizing: 'border-box' },
          }}
        >
          <SidebarMenu selected={selected} onSelect={setSelected} />
        </Drawer>

        {/* Sidebar for mobile */}
        <Drawer
          variant="temporary"
          open={mobileOpen}
          onClose={handleDrawerToggle}
          ModalProps={{ keepMounted: true }} // Better mobile performance
          sx={{
            display: { xs: 'block', md: 'none' },
            '& .MuiDrawer-paper': { width: drawerWidth, boxSizing: 'border-box' },
          }}
        >
          <SidebarMenu selected={selected} onSelect={setSelected} />
        </Drawer>
      </Box>

      {/* Main content */}
      <Box
        component="main"
        sx={{
          flexGrow: 1,
          minHeight: '100vh',
          p: { xs: 2, sm: 3 },
          width: { md: `calc(100% - ${drawerWidth}px)` },
        }}
      >
        {/* Hamburger toggle for mobile */}
        {!upMd && (
          <IconButton
            color="inherit"
            aria-label="open drawer"
            edge="start"
            onClick={handleDrawerToggle}
            sx={{ mb: 2 }}
          >
            <MenuIcon />
          </IconButton>
        )}

        {renderContent ? renderContent(selected) : <div>NO RENDERCONTENT</div>}
      </Box>
    </Box>
  );
};
