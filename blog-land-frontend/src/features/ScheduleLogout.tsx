import { AppDispatch } from '../store/store';
import { logout } from '../store/authSlice';

export const scheduleLogout = (expiresIn: number, dispatch: AppDispatch) => {
  if (!expiresIn || expiresIn <= 0) {
    dispatch(logout());
    return;
  }

  setTimeout(() => {
    dispatch(logout());
  }, expiresIn);
};
