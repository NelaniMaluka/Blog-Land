import { Route, Routes, useLocation } from 'react-router-dom';
import './App.css';
import MainRoutes from './Routes/MainRoutes';
import { useEffect } from 'react';
import Layout from './components/Layout';

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
        <Route path="/" element={<Layout noLayout={false} />}>
          {MainRoutes()}
        </Route>
      </Routes>
    </>
  );
}

export default App;
