import { Route } from 'react-router-dom';
import HomePage from '../layout/HomePage';
import { ROUTES } from '../constants/routes';
import { TrendingPage } from '../layout/TrendingPage';
import { ViewAllPage } from '../layout/ViewAllPage';
import { CategoryPage } from '../layout/CategoryPage';
import { LatestPage } from '../layout/LatestPage';

const MainRoutes = () => (
  <>
    <Route path={ROUTES.HOME} element={<HomePage />} />
    <Route path={ROUTES.TRENDING_POSTS} element={<TrendingPage />} />
    <Route path={ROUTES.VIEW_ALL} element={<ViewAllPage />} />
    <Route path={ROUTES.LATEST_POSTS} element={<LatestPage />} />
    <Route path="/category/:slug" element={<CategoryPage />} />
  </>
);

export default MainRoutes;
