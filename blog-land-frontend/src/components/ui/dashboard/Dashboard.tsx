import * as React from 'react';
import { Box, CssBaseline, Drawer, useMediaQuery } from '@mui/material';
import { useTheme } from '@mui/material/styles';
import { AppBarDashboard } from './nav/NavBar';
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

      {/* Top AppBar */}
      <AppBarDashboard onMenuClick={handleDrawerToggle} />

      {/* Sidebar */}
      <Box component="nav" sx={{ width: { md: drawerWidth }, flexShrink: { md: 0 } }}>
        <Drawer
          variant="temporary"
          open={mobileOpen}
          onClose={handleDrawerToggle}
          ModalProps={{ keepMounted: true }}
          sx={{
            display: { xs: 'block', md: 'none' },
            '& .MuiDrawer-paper': { width: drawerWidth },
          }}
        >
          <Box sx={{ height: 64 }} /> {/* Push below AppBar */}
          <SidebarMenu selected={selected} onSelect={setSelected} />
        </Drawer>

        <Drawer
          variant="permanent"
          open
          sx={{
            display: { xs: 'none', md: 'block' },
            '& .MuiDrawer-paper': { width: drawerWidth, boxSizing: 'border-box' },
          }}
        >
          <Box sx={{ height: 64 }} /> {/* Push below AppBar */}
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
          mt: { xs: '56px', md: '64px' },
          width: { md: `calc(100% - ${drawerWidth}px)` },
        }}
      >
        {renderContent ? renderContent(selected) : <div>NO RENDERCONTENT</div>}
      </Box>
    </Box>
  );
};
