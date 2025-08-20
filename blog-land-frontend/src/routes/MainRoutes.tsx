import { Route } from 'react-router-dom';
import HomePage from '../layout/HomePage';
import { ROUTES } from '../constants/routes';
import { TrendingPage } from '../layout/TrendingPage';
import { ViewAllPage } from '../layout/ViewAllPage';
import { CategoryPage } from '../layout/CategoryPage';
import { LatestPage } from '../layout/LatestPage';
import { PostPage } from '../layout/PostPage';
import { RandomPostPage } from '../layout/RandomPostPage';
import { LatestPostPage } from '../layout/LatestPostPage';
import { AboutPage } from '../layout/AboutPage';
import { TermsAndServices } from '../layout/TermsAndServicesPage';
import { PrivacyPolicy } from '../layout/PrivacyPolicy';

const MainRoutes = () => (
  <>
    <Route path={ROUTES.HOME} element={<HomePage />} />
    <Route path={ROUTES.ABOUT} element={<AboutPage />} />
    <Route path={ROUTES.TERMS_AND_CONDITIONS} element={<TermsAndServices />} />
    <Route path={ROUTES.PRIVACY_POLICY} element={<PrivacyPolicy />} />
    <Route path={ROUTES.TRENDING_POSTS} element={<TrendingPage />} />
    <Route path={ROUTES.VIEW_ALL} element={<ViewAllPage />} />
    <Route path={ROUTES.LATEST_POSTS} element={<LatestPage />} />
    <Route path="/category/:slug" element={<CategoryPage />} />
    <Route path="/post/:slug" element={<PostPage />} />
    <Route path={ROUTES.RANDOM_POSTS} element={<RandomPostPage />} />
    <Route path="/latest/post/:slug" element={<LatestPostPage />} />
  </>
);

export default MainRoutes;
