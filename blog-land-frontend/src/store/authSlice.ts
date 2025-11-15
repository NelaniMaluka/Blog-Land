import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { LoginResponse, UserResponse } from '../types/user/response';

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
    setLoginResponse: (state, action: PayloadAction<LoginResponse>) => {
      state.jwtToken = action.payload.token;
      state.user = action.payload.user;
      state.isAuthenticated = true;
    },
    logout: (state) => {
      state.jwtToken = null;
      state.user = null;
      state.isAuthenticated = false;
    },
  },
});

export const { setToken, setUser, setLoginResponse, logout } = authSlice.actions;
export default authSlice.reducer;
