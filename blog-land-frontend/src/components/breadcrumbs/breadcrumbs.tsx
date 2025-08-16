import * as React from 'react';
import Typography from '@mui/material/Typography';
import Breadcrumbs from '@mui/material/Breadcrumbs';
import Link from '@mui/material/Link';
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
        separator={
          <span
            style={{ fontSize: '0.6rem', margin: '0px', lineHeight: 1, verticalAlign: 'middle' }}
          >
            /
          </span>
        } // smaller separator
        sx={{ alignItems: 'center' }} // vertical alignment
      >
        <Link
          underline="hover"
          color="inherit"
          href={ROUTES.HOME}
          sx={{ fontSize: '0.6rem', lineHeight: 1, verticalAlign: 'middle' }}
        >
          Blog-Land
        </Link>
        <Link
          underline="hover"
          color="inherit"
          href={link || '/'}
          sx={{ fontSize: '0.6rem', lineHeight: 1, verticalAlign: 'middle' }}
        >
          {title}
        </Link>
        <Typography
          sx={{ color: 'text.primary', fontSize: '0.6rem', lineHeight: 1, verticalAlign: 'middle' }}
        >
          {page}
        </Typography>
      </Breadcrumbs>
    </div>
  );
};

export default BasicBreadcrumbs;
