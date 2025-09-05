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
    const delta = 1; // how many pages around current page to show

    const left = Math.max(2, page + 1 - delta); // start of middle section
    const right = Math.min(totalPages - 1, page + 1 + delta); // end of middle section

    pages.push(1); // first page

    if (left > 2) pages.push('...'); // left ellipsis

    for (let i = left; i <= right; i++) {
      pages.push(i);
    }

    if (right < totalPages - 1) pages.push('...'); // right ellipsis

    if (totalPages > 1) pages.push(totalPages); // last page

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
