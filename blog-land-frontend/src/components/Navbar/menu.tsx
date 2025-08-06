import * as React from 'react';
import Box from '@mui/joy/Box';
import Drawer from '@mui/joy/Drawer';
import List from '@mui/joy/List';
import ListItemButton from '@mui/joy/ListItemButton';
import Typography from '@mui/joy/Typography';
import ModalClose from '@mui/joy/ModalClose';
import CategoryIcon from '@mui/icons-material/Category';
import ChevronRightIcon from '@mui/icons-material/ChevronRight';

import Search from '@mui/icons-material/Search';
import AddCircleOutlineIcon from '@mui/icons-material/AddCircleOutline';
import ShuffleIcon from '@mui/icons-material/Shuffle';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import WhatshotIcon from '@mui/icons-material/Whatshot';

import { useCategories } from '../../hooks/useCategory';
import { CategoryResponse } from '../../types/api';
import { useEffect } from 'react';

import { useTheme, useMediaQuery } from '@mui/material';

interface DrawerMobileNavigationProps {
  open: boolean;
  setOpen: (open: boolean) => void;
}

export default function DrawerMobileNavigation({ open, setOpen }: DrawerMobileNavigationProps) {
  const { categoryData, loading, error, setError, getCategories } =
    useCategories<CategoryResponse>();

  const theme = useTheme();
  const isMobileOrTablet = useMediaQuery(theme.breakpoints.down('md'));

  // Run once on page load
  useEffect(() => {
    getCategories();
  }, [getCategories]);

  // Run again when the drawer is opened
  useEffect(() => {
    if (open) {
      getCategories();
    }
  }, [open, getCategories]);

  return (
    <Drawer open={open} onClose={() => setOpen(false)}>
      {/* Close button */}
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5, ml: 'auto', mt: 1, mr: 2 }}>
        <Typography
          component="label"
          htmlFor="close-icon"
          sx={{ fontSize: 'sm', fontWeight: 'lg', cursor: 'pointer' }}
        />
        <ModalClose id="close-icon" sx={{ position: 'initial' }} />
      </Box>

      {/* Tablet/Mobile only nav links */}
      {isMobileOrTablet && (
        <Box sx={{ px: 2, mb: 2 }}>
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
            <a
              href="/random/post"
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: 1,
                textDecoration: 'none',
                color: 'inherit',
              }}
            >
              <ShuffleIcon fontSize="small" sx={{ fontSize: '0.8rem', marginRight: '5px' }} />
              <Typography sx={{ fontSize: '0.8rem' }}>Random</Typography>
            </a>
            <a
              href="/latest/posts"
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: 1,
                textDecoration: 'none',
                color: 'inherit',
              }}
            >
              <AccessTimeIcon fontSize="small" sx={{ fontSize: '0.8rem', marginRight: '5px' }} />
              <Typography sx={{ fontSize: '0.8rem' }}>Latest</Typography>
            </a>
            <a
              href="/trending/posts"
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: 1,
                textDecoration: 'none',
                color: 'inherit',
              }}
            >
              <WhatshotIcon fontSize="small" sx={{ fontSize: '0.8rem', marginRight: '5px' }} />
              <Typography sx={{ fontSize: '0.8rem' }}>Trending</Typography>
            </a>
          </Box>
        </Box>
      )}

      {/* Categories header */}
      <h2
        style={{
          fontSize: '1.2rem',
          textAlign: 'center',
          fontWeight: '400',
          borderTop: '0.5px solid black',
          padding: '20px 0 10px 0',
        }}
      >
        Categories
      </h2>

      {/* Categories list */}
      <List
        size="lg"
        component="nav"
        sx={{ flex: 'none', fontSize: 'xl', '& > div': { justifyContent: 'flex-start' } }}
      >
        {categoryData?.length ? (
          categoryData.map((category, index) => {
            const key =
              typeof category === 'string'
                ? `string-${index}-${category}`
                : category.categoryId ?? `category-${index}`;
            return (
              <ListItemButton
                key={key}
                sx={{
                  fontSize: '0.8rem',
                  py: '4px',
                  px: '12px',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'space-between', // left and right edges
                }}
              >
                {/* Left side: icon + text */}
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                  <CategoryIcon fontSize="small" sx={{ height: '0.8rem', minWidth: '1rem' }} />
                  <Typography component="span" sx={{ lineHeight: 1, fontSize: '0.8rem' }}>
                    {typeof category === 'string' ? category : category.name}
                  </Typography>
                </Box>

                {/* Right side: post count */}
                <Typography
                  component="span"
                  sx={{
                    fontSize: '0.75rem',
                    color: 'text.secondary',
                  }}
                >
                  {category.postCount}
                </Typography>
              </ListItemButton>
            );
          })
        ) : (
          <ListItemButton key="no-data" disabled>
            <Typography
              level="body-sm"
              sx={{ fontSize: '0.7rem', textAlign: 'center', width: '100%' }}
            >
              No categories available
            </Typography>
          </ListItemButton>
        )}
      </List>
    </Drawer>
  );
}
