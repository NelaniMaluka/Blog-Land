import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { UserResponse } from '../types/user/response';

interface AuthState {
  jwtToken: string | null;
  user: UserResponse | null;
  isAuthenticated: boolean;
}

const initialState: AuthState = {
  jwtToken: null,
  user: null,
  isAuthenticated: false,
};

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    setToken: (state, action: PayloadAction<string>) => {
      state.jwtToken = action.payload;
      state.isAuthenticated = true;
    },
    setUser: (state, action: PayloadAction<UserResponse>) => {
      state.user = action.payload;
    },
    logout: (state) => {
      state.jwtToken = null;
      state.user = null;
      state.isAuthenticated = false;
    },
  },
});

export const { setToken, setUser, logout } = authSlice.actions;
export default authSlice.reducer;
