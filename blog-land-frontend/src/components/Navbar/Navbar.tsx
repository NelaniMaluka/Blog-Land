import styles from './Navbar.module.css';

import AvatarMenu from './Avatar/Avatar';
import NavbarBottom from './NavbarBottom/NavbarBottom';
import { ROUTES } from '../../constants/routes';

interface NavbarProps {
  setOpen: (open: boolean) => void;
}

function Navbar({ setOpen }: NavbarProps) {
  return (
    <>
      <nav className={styles.navbar}>
        <div className="container">
          <div className={`${styles.navbar_row} ${styles['navbar_row--top']}`}>
            <div className={styles.navbar_Logo}>
              <img
                src="/icons/menu.png"
                alt="menu icon"
                onClick={() => setOpen(true)}
                className={styles.menu}
              />
              <a href={ROUTES.HOME}>
                <h2>Blog-Land</h2>
              </a>
            </div>
            <div className={styles.navbar_Links}>
              <span className={styles.navbar_Link_mobile}>
                <a href={ROUTES.VIEW_ALL}>
                  <img className={styles.list} src="/icons/list.png" alt="View-All icon" />
                  <span>View All</span>
                </a>
              </span>
              <span className={styles.navbar_Link_mobile}>
                <a href={ROUTES.RANDOM_POSTS}>
                  <img src="/icons/random.png" alt="Random icon" />
                  <span>Random</span>
                </a>
              </span>
              <span className={styles.navbar_Link_mobile}>
                <a href={ROUTES.LATEST_POSTS}>
                  <img className={styles.clock} src="/icons/clock.png" alt="Latest icon" />
                  <span>Latest</span>
                </a>
              </span>
              <span className={styles.navbar_Link_mobile}>
                <a href={ROUTES.TRENDING_POSTS}>
                  <img className={styles.trending} src="/icons/trending.png" alt="trending icon" />
                  <span>Trending</span>
                </a>
              </span>
              <span className="navbar__link">
                <AvatarMenu />
              </span>
            </div>
          </div>
          <div className={`${styles.navbar_row} ${styles['navbar_row--bottom']}`}>
            <NavbarBottom />
          </div>
        </div>
      </nav>
    </>
  );
}

export default Navbar;
