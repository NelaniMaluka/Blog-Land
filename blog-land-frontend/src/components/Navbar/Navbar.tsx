import styles from './Navbar.module.css';

import AddCircleOutlineIcon from '@mui/icons-material/AddCircleOutline';
import ShuffleIcon from '@mui/icons-material/Shuffle';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import WhatshotIcon from '@mui/icons-material/Whatshot';
import AvatarMenu from './Avatar';
import SearchBar from './searchbar/Searchbar';
import MenuIcon from '@mui/icons-material/Menu';
import IconButton from '@mui/material/IconButton';

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
              <IconButton onClick={() => setOpen(true)} edge="start" sx={{ color: 'inherit' }}>
                <MenuIcon />
              </IconButton>
              <a href="/">
                <h1>Blog-Land</h1>
              </a>
            </div>
            <div className={styles.navbar_Links}>
              <span className={styles.navbar_Link_mobile}>
                <a href="/dashboard/posts/add-post">
                  <AddCircleOutlineIcon fontSize="small" />
                  <span>Add Post</span>
                </a>
              </span>
              <span className={styles.navbar_Link_mobile}>
                <a href="/random/post">
                  <ShuffleIcon fontSize="small" />
                  <span>Random</span>
                </a>
              </span>
              <span className={styles.navbar_Link_mobile}>
                <a href="/latest/posts">
                  <AccessTimeIcon fontSize="small" />
                  <span>Latest</span>
                </a>
              </span>
              <span className={styles.navbar_Link_mobile}>
                <a href="/trending/posts">
                  <WhatshotIcon fontSize="small" />
                  <span>Trending</span>
                </a>
              </span>
              <span className="navbar__link">
                <AvatarMenu />
              </span>
            </div>
          </div>
          <div className={`${styles.navbar_row} ${styles['navbar_row--bottom']}`}>
            <SearchBar />
          </div>
        </div>
      </nav>
    </>
  );
}

export default Navbar;
