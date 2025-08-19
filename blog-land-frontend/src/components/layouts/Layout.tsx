import { Outlet } from 'react-router-dom';
import Navbar from '../Navbar/Navbar';
import { ReactNode } from 'react';
import { useState } from 'react';
import DrawerMobileNavigation from '../Navbar/menu/menu';
import { GlobalPreloadQueries } from '../../features/GlobalPreloadQueries';
import Footer from '../Footer/Footer';

interface LayoutProps {
  noLayout?: boolean;
  children?: ReactNode;
}

export default function Layout({ noLayout, children }: LayoutProps) {
  const [open, setOpen] = useState(false);

  return (
    <div>
      {!noLayout && (
        <>
          <GlobalPreloadQueries />
          <Navbar setOpen={setOpen} />
          <DrawerMobileNavigation open={open} setOpen={setOpen} />
        </>
      )}
      <main>{children ?? <Outlet />}</main>
      {!noLayout && <footer></footer>}
      <Footer />
    </div>
  );
}
