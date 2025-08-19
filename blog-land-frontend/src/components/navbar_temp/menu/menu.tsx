import * as React from 'react';
import Box from '@mui/joy/Box';
import Drawer from '@mui/joy/Drawer';
import List from '@mui/joy/List';
import ListItemButton from '@mui/joy/ListItemButton';
import Typography from '@mui/joy/Typography';
import ModalClose from '@mui/joy/ModalClose';

import { useGetCategories } from '../../../hooks/useCategory';
import LoadingScreen from '../../../features/LoadingScreen/LoadingScreen';
import { ROUTES } from '../../../constants/routes';

import styles from './menu.module.css';
import { Link } from 'react-router-dom';

interface DrawerMobileNavigationProps {
  open: boolean;
  setOpen: (open: boolean) => void;
}

export default function DrawerMobileNavigation({ open, setOpen }: DrawerMobileNavigationProps) {
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
        <>
          <h2 className={styles.categoriesHeader}>Discover</h2>
          <Box className={styles.navLinks}>
            <Box className={styles.navLinksColumn}>
              <a href={ROUTES.VIEW_ALL} className={styles.navLink}>
                <img src="/icons/list.png" alt="View-All icon" className={styles.navLinkIcon} />
                <Typography className={styles.navLinkText}>View All</Typography>
              </a>

              <a href={ROUTES.RANDOM_POSTS} className={styles.navLink}>
                <img src="/icons/random.png" alt="Random icon" className={styles.navLinkIcon} />
                <Typography className={styles.navLinkText}>Random</Typography>
              </a>

              <a href={ROUTES.LATEST_POSTS} className={styles.navLink}>
                <img src="/icons/clock.png" alt="Latest icon" className={styles.navLinkIcon} />
                <Typography className={styles.navLinkText}>Latest</Typography>
              </a>

              <a href={ROUTES.TRENDING_POSTS} className={styles.navLink}>
                <img src="/icons/trending.png" alt="Trending icon" className={styles.navLinkIcon} />
                <Typography className={styles.navLinkText}>Trending</Typography>
              </a>
            </Box>
          </Box>
        </>

        {/* Categories header */}
        <h2 className={styles.categoriesHeader}>Categories</h2>

        {/* Categories list */}
        <List size="lg" component="nav" className={styles.listRoot}>
          {data?.length ? (
            data.map((category, index) => (
              <Link
                key={category.id}
                to={ROUTES.CATEGORY_POSTS(category.name)}
                onClick={() => setOpen(false)}
              >
                <ListItemButton className={styles.listItemButton}>
                  <Box className={styles.listItemLeft}>
                    <Typography component="span" className={styles.categoryName}>
                      <img
                        src="/icons/category.png"
                        alt="category icon"
                        className={styles.navLinkIcon}
                      />
                      <span className={styles.navLinkText}>{category.name}</span>
                    </Typography>
                  </Box>
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
