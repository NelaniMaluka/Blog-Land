import * as jwtDecode from 'jwt-decode';
import { AppDispatch } from '../store/store';
import { logout } from '../store/authSlice';

interface JwtPayload {
  exp: number;
}

export const scheduleLogout = (token: string, dispatch: AppDispatch) => {
  const decoded = (jwtDecode as unknown as (token: string) => JwtPayload)(token);

  const expirationTimeMs = decoded.exp * 1000 - Date.now();

  if (expirationTimeMs <= 0) {
    dispatch(logout());
    return;
  }

  setTimeout(() => {
    dispatch(logout());
  }, expirationTimeMs);
};
