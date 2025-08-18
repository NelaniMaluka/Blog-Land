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
import ViewListIcon from '@mui/icons-material/ViewList';

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

  const { data, isLoading } = useGetCategories();

  return (
    <LoadingScreen isLoading={isLoading}>
      <Drawer open={open} onClose={() => setOpen(false)}>
        {/* Close button */}
        <Box className={styles.closeButtonContainer}>
          <Typography component="label" htmlFor="close-icon" className={styles.closeButtonLabel} />
          <ModalClose id="close-icon" className={styles.closeButtonLabel} />
        </Box>

        {/* Mobile nav links */}
        {isMobileOrTablet && (
          <>
            <h2 className={styles.categoriesHeader}>Discover</h2>
            <Box className={styles.navLinks}>
              <Box className={styles.navLinksColumn}>
                <a href={ROUTES.VIEW_ALL} className={styles.navLink}>
                  <ViewListIcon className={styles.navLinkIcon} />
                  <Typography className={styles.navLinkText}>View All</Typography>
                </a>

                <a href={ROUTES.RANDOM_POSTS} className={styles.navLink}>
                  <ShuffleIcon className={styles.navLinkIcon} />
                  <Typography className={styles.navLinkText}>Random</Typography>
                </a>

                <a href={ROUTES.LATEST_POSTS} className={styles.navLink}>
                  <AccessTimeIcon className={styles.navLinkIcon} />
                  <Typography className={styles.navLinkText}>Latest</Typography>
                </a>

                <a href={ROUTES.TRENDING_POSTS} className={styles.navLink}>
                  <WhatshotIcon className={styles.navLinkIcon} />
                  <Typography className={styles.navLinkText}>Trending</Typography>
                </a>
              </Box>
            </Box>
          </>
        )}

        {/* Categories header */}
        <h2 className={styles.categoriesHeader}>Categories</h2>

        {/* Categories list */}
        <List size="lg" component="nav" className={styles.listRoot}>
          {data?.length ? (
            data.map((category, index) => (
              <Link key={category.id} to={ROUTES.CATEGORY_POSTS(category.name)}>
                <ListItemButton className={styles.listItemButton}>
                  <Box className={styles.listItemLeft}>
                    <ReorderIcon className={colorClasses[index % colorClasses.length]} />
                    <Typography component="span" className={styles.categoryName}>
                      <span>{category.name}</span>
                    </Typography>
                  </Box>
                  <Typography component="span" className={styles.postCount}>
                    <span>{category.postCount}</span>
                  </Typography>
                </ListItemButton>
              </Link>
            ))
          ) : (
            <ListItemButton disabled className={styles.listItemButton}>
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
