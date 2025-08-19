import { Route, Routes, useLocation } from 'react-router-dom';
import './App.css';
import MainRoutes from './Routes/MainRoutes';
import DashboardRoutes from './Routes/DashboardRoutes';
import { useEffect } from 'react';
import Layout from './components/layouts/Layout';
import { ROUTES } from './constants/routes';

// ScrollToTop Component
const ScrollToTop = () => {
  const { pathname } = useLocation();

  useEffect(() => {
    window.scrollTo(0, 0);
  }, [pathname]);

  return null;
};

function App() {
  return (
    <>
      <ScrollToTop />

      <Routes>
        {/* All pages that should use Navbar + Footer */}
        <Route path={ROUTES.HOME} element={<Layout noLayout={false} />}>
          {MainRoutes()}
        </Route>

        {/* Dashboard with no Navbar/Footer */}
        <Route path={ROUTES.DASHBOARD_PROFILE} element={<Layout noLayout />}>
          {DashboardRoutes()}
        </Route>
      </Routes>
    </>
  );
}

export default App;
