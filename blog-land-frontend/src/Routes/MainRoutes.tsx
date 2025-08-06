import { Route } from 'react-router-dom';
import HomePage from '../layout/HomePage';

const MainRoutes = () => (
  <>
    <Route path="/" element={<HomePage />} />
  </>
);

export default MainRoutes;
