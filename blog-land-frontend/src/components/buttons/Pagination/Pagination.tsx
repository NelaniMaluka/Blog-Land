import React from 'react';
import Pagination from '@mui/material/Pagination';
import PaginationItem from '@mui/material/PaginationItem';
import styles from './Pagination.module.css';

interface CustomPaginationProps {
  page: number; // 0-based current page
  setPage: (page: number) => void;
  totalPages: number;
}

const CustomPagination: React.FC<CustomPaginationProps> = ({ page, setPage, totalPages }) => {
  if (totalPages <= 1) return null;

  const getPages = (): (number | '...')[] => {
    const pages: (number | '...')[] = [];

    if (page <= 1) {
      // Start
      pages.push(1, 2, 3);
      if (totalPages > 4) pages.push('...');
      if (totalPages > 3) pages.push(totalPages);
    } else if (page >= totalPages - 2) {
      // End
      pages.push(1);
      if (totalPages > 4) pages.push('...');
      pages.push(totalPages - 2, totalPages - 1, totalPages);
    } else {
      // Middle
      pages.push(1, '...');
      pages.push(page, page + 1, page + 2);
      pages.push('...', totalPages);
    }

    return pages;
  };

  const pages = getPages();

  return (
    <div className={styles.pagination}>
      <Pagination
        count={totalPages}
        page={page + 1}
        onChange={(_, value) => setPage(value - 1)}
        hideNextButton
        hidePrevButton
        renderItem={(item) => {
          const pageNum = item.page;

          if (pageNum === null) {
            return <PaginationItem {...item} disabled />;
          }

          if (pages.includes(pageNum)) {
            return <PaginationItem {...item} />;
          }

          return null;
        }}
      />
    </div>
  );
};

export default CustomPagination;
