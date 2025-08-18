import * as React from 'react';
import Typography from '@mui/material/Typography';
import Breadcrumbs from '@mui/material/Breadcrumbs';
import Link from '@mui/material/Link';
import styles from './breadcrumbs.module.css';
import { ROUTES } from '../../constants/routes';

interface PostsLayoutProps {
  title: string;
  link: string;
  page: string;
}

export const BasicBreadcrumbs: React.FC<PostsLayoutProps> = ({ title, link, page }) => {
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
        <Link underline="hover" color="inherit" href={link || '/'} className={styles.crumb}>
          {title}
        </Link>
        <Typography className={styles.primary}>{page}</Typography>
      </Breadcrumbs>
    </div>
  );
};

export default BasicBreadcrumbs;
