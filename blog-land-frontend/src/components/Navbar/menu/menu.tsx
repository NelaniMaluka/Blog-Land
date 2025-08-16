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
import ReorderIcon from '@mui/icons-material/Reorder';

import { useGetCategories } from '../../../hooks/useCategory';
import LoadingScreen from '../../../features/LoadingScreen/LoadingScreen';
import { useTheme, useMediaQuery } from '@mui/material';
import { ROUTES } from '../../../constants/routes';

import styles from './menu.module.css';
import { Link } from 'react-router-dom';

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
          <>
            <h2 className={styles.categoriesHeader}>Discover</h2>

            <Box className={styles.navLinks}>
              <Box
                sx={{ display: 'flex', flexDirection: 'column', gap: '0.5rem', margin: '0.7rem 0' }}
              >
                <a href={ROUTES.RANDOM_POSTS} className={styles.navLink}>
                  <ShuffleIcon
                    fontSize="small"
                    sx={{ fontSize: '0.7rem', marginRight: '0.5rem' }}
                  />
                  <Typography sx={{ fontSize: '0.7rem' }}>Random</Typography>
                </a>
                <a href={ROUTES.LATEST_POSTS} className={styles.navLink}>
                  <AccessTimeIcon
                    fontSize="small"
                    sx={{ fontSize: '0.7rem', marginRight: '0.5rem' }}
                  />
                  <Typography sx={{ fontSize: '0.7rem' }}>Latest</Typography>
                </a>
                <a href={ROUTES.TRENDING_POSTS} className={styles.navLink}>
                  <WhatshotIcon
                    fontSize="small"
                    sx={{ fontSize: '0.7rem', marginRight: '0.5rem' }}
                  />
                  <Typography sx={{ fontSize: '0.7rem' }}>Trending</Typography>
                </a>
              </Box>
            </Box>
          </>
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
                  : category.id ?? `category-${index}`;
              return (
                <Link key={category.id} to={`/category/${encodeURIComponent(category.name)}`}>
                  <ListItemButton key={key} className={styles.listItemButton}>
                    {/* Left side: icon + text */}
                    <Box className={styles.listItemLeft}>
                      <ReorderIcon
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
                </Link>
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
