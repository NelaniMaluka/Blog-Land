import * as React from 'react';
import Typography from '@mui/material/Typography';
import Breadcrumbs from '@mui/material/Breadcrumbs';
import Link from '@mui/material/Link';
import styles from './breadcrumbs.module.css';
import { ROUTES } from '../../constants/routes';
import { useTheme } from '@mui/material/styles';
import useMediaQuery from '@mui/material/useMediaQuery';

interface PostsLayoutProps {
  page1?: string;
  link1?: string;
  page2?: string;
}

export const BasicBreadcrumbs: React.FC<PostsLayoutProps> = ({ page1, link1, page2 }) => {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));

  return (
    <div role="presentation">
      <Breadcrumbs
        aria-label="breadcrumb"
        separator={<span className={styles.seperator}>/</span>}
        sx={{ alignItems: 'center' }}
      >
        <Link underline="hover" color="inherit" href={ROUTES.HOME} className={styles.crumb}>
          Blog-Land
        </Link>
        {!page1 && !page2 && <Typography className={styles.primary}>Post</Typography>}
        {page1 &&
          !page2 && [
            <Link underline="hover" color="inherit" href={ROUTES.VIEW_ALL} className={styles.crumb}>
              Post
            </Link>,
            <Typography className={styles.primary}>{page1}</Typography>,
          ]}
        {page2 && [
          <Link underline="hover" color="inherit" href={ROUTES.VIEW_ALL} className={styles.crumb}>
            Post
          </Link>,
          !isMobile && (
            <Link
              key="page1"
              underline="hover"
              color="inherit"
              href={link1}
              className={styles.crumb}
            >
              {page1}
            </Link>
          ),
          <Typography key="page2" className={`${styles.primary} ${styles.ellipsis}`}>
            {isMobile ? page1 : page2}
          </Typography>,
        ]}
      </Breadcrumbs>
    </div>
  );
};

export default BasicBreadcrumbs;
