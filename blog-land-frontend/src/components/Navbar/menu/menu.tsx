import * as React from 'react';
import Box from '@mui/joy/Box';
import Drawer from '@mui/joy/Drawer';
import List from '@mui/joy/List';
import ListItemButton from '@mui/joy/ListItemButton';
import Typography from '@mui/joy/Typography';
import ModalClose from '@mui/joy/ModalClose';
import CategoryIcon from '@mui/icons-material/Category';
import ShuffleIcon from '@mui/icons-material/Shuffle';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import WhatshotIcon from '@mui/icons-material/Whatshot';

import { useGetCategories } from '../../../hooks/useCategory';
import LoadingScreen from '../../../features/LoadingScreen/LoadingScreen';
import { useTheme, useMediaQuery } from '@mui/material';

import styles from './menu.module.css';

interface DrawerMobileNavigationProps {
  open: boolean;
  setOpen: (open: boolean) => void;
}

export default function DrawerMobileNavigation({ open, setOpen }: DrawerMobileNavigationProps) {
  const theme = useTheme();
  const isMobileOrTablet = useMediaQuery(theme.breakpoints.down('md'));
  const colorClasses = [styles.categoryIconBlue, styles.categoryIconRed, styles.categoryIconGreen];

  const { data, isLoading, error } = useGetCategories();

  return (
    <LoadingScreen isLoading={isLoading}>
      <Drawer open={open} onClose={() => setOpen(false)}>
        {/* Close button */}
        <Box className={styles.closeButtonContainer}>
          <Typography
            component="label"
            htmlFor="close-icon"
            sx={{ fontSize: 'sm', fontWeight: 'lg', cursor: 'pointer' }}
          />
          <ModalClose id="close-icon" sx={{ position: 'initial' }} />
        </Box>

        {/* Tablet/Mobile only nav links */}
        {isMobileOrTablet && (
          <Box className={styles.navLinks}>
            <Box
              sx={{ display: 'flex', flexDirection: 'column', gap: '0.5rem', margin: '0.7rem 0' }}
            >
              <a href="/random/post" className={styles.navLink}>
                <ShuffleIcon fontSize="small" sx={{ fontSize: '0.9rem', marginRight: '5px' }} />
                <Typography sx={{ fontSize: '0.9rem' }}>Random</Typography>
              </a>
              <a href="/latest/posts" className={styles.navLink}>
                <AccessTimeIcon fontSize="small" sx={{ fontSize: '0.9rem', marginRight: '5px' }} />
                <Typography sx={{ fontSize: '0.9rem' }}>Latest</Typography>
              </a>
              <a href="/trending/posts" className={styles.navLink}>
                <WhatshotIcon fontSize="small" sx={{ fontSize: '0.9rem', marginRight: '5px' }} />
                <Typography sx={{ fontSize: '0.9rem' }}>Trending</Typography>
              </a>
            </Box>
          </Box>
        )}

        {/* Categories header */}
        <h2 className={styles.categoriesHeader}>Categories</h2>

        {/* Categories list */}
        <List size="lg" component="nav" sx={{ flex: 'none', fontSize: 'xl' }}>
          {data?.length ? (
            data.map((category, index) => {
              const key =
                typeof category === 'string'
                  ? `string-${index}-${category}`
                  : category.categoryId ?? `category-${index}`;
              return (
                <ListItemButton key={key} className={styles.listItemButton}>
                  {/* Left side: icon + text */}
                  <Box className={styles.listItemLeft}>
                    <CategoryIcon
                      fontSize="small"
                      className={colorClasses[index % colorClasses.length]}
                      sx={{ height: '0.8rem', minWidth: '1rem' }}
                    />
                    <Typography component="span" className={styles.categoryName}>
                      <span>{typeof category === 'string' ? category : category.name}</span>
                    </Typography>
                  </Box>

                  {/* Right side: post count */}
                  <Typography component="span" className={styles.postCount}>
                    <span>{category.postCount}</span>
                  </Typography>
                </ListItemButton>
              );
            })
          ) : (
            <ListItemButton key="no-data" disabled>
              <Typography level="body-sm" className={styles.noCategories}>
                No categories available
              </Typography>
            </ListItemButton>
          )}
        </List>
      </Drawer>
    </LoadingScreen>
  );
}
