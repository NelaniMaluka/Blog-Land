import { DashboardPage } from '../layout/DashboardPage';
import { Route } from 'react-router-dom';
import { ROUTES } from '../constants/routes';

const DashboardRoutes = () => (
  <>
    <Route path={ROUTES.DASHBOARD} element={<DashboardPage />} />
  </>
);

export default DashboardRoutes;
