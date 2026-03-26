import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { authApi } from '../../api/services';

export const login = createAsyncThunk('auth/login', async (credentials, { rejectWithValue }) => {
  try {
    const { data } = await authApi.login(credentials);
    localStorage.setItem('accessToken', data.accessToken);
    localStorage.setItem('refreshToken', data.refreshToken);
    return data;
  } catch (err) {
    return rejectWithValue(err.response?.data?.message || 'Login failed');
  }
});

const storedToken = localStorage.getItem('accessToken');
let storedUser = null;
try { storedUser = JSON.parse(localStorage.getItem('user')); } catch {}

const authSlice = createSlice({
  name: 'auth',
  initialState: {
    user: storedUser,
    token: storedToken,
    loading: false,
    error: null,
  },
  reducers: {
    logout: (state) => {
      state.user = null;
      state.token = null;
      localStorage.clear();
    },
    setUser: (state, action) => {
      state.user = action.payload;
      localStorage.setItem('user', JSON.stringify(action.payload));
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(login.pending, (state) => { state.loading = true; state.error = null; })
      .addCase(login.fulfilled, (state, action) => {
        state.loading = false;
        state.token = action.payload.accessToken;
        state.user = {
          id: action.payload.id,
          email: action.payload.email,
          role: action.payload.role,
          passwordChanged: action.payload.passwordChanged,
        };
        localStorage.setItem('user', JSON.stringify(state.user));
      })
      .addCase(login.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      });
  },
});

export const { logout, setUser } = authSlice.actions;
export default authSlice.reducer;