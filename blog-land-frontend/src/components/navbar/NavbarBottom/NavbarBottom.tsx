// NavbarBottom.tsx
import * as React from 'react';
import styles from './NavbarBottom.module.css';
import SearchBar from '../searchbar/Searchbar';
import { useGetCategories } from '../../../hooks/useCategory';
import LoadingScreen from '../../../features/LoadingScreen/LoadingScreen';
import { Link } from 'react-router-dom';
import { ROUTES } from '../../../constants/routes';

export default function NavbarBottom() {
  const { data: categories, isLoading, isError } = useGetCategories();
  return (
    <div className={`${styles.navbar_row} ${styles['navbar_row--bottom']}`}>
      <LoadingScreen isLoading={isLoading}>
        <div className={styles.dropdown}>
          <span className={styles.dropdownTitle}>Categories</span>{' '}
          <img src="/icons/down-arrow.png" alt="down-arrow" />
          <ul className={styles.dropdownMenu}>
            {categories?.map((cat) => (
              <Link key={cat.id} to={ROUTES.CATEGORY_POSTS(cat.name)}>
                {cat.name}
              </Link>
            ))}
          </ul>
        </div>
      </LoadingScreen>
      <div className={styles.searchbar}>
        <SearchBar />
      </div>
    </div>
  );
}
