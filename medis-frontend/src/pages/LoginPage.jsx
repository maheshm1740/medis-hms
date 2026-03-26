import { useState } from 'react';
import { useDispatch } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { Box, Typography, TextField, Button, CircularProgress } from '@mui/material';
import { login } from '../app/slices/authSlice';
import { ErrorMessage } from '../components/common';
import { Link as RouterLink } from 'react-router-dom';
import { Link } from '@mui/material';

export default function LoginPage() {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const [form, setForm] = useState({ email: '', password: '' });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    const result = await dispatch(login(form));
    setLoading(false);
    if (login.fulfilled.match(result)) {
      if (!result.payload.passwordChanged) {
        navigate('/change-password');
      } else {
        navigate('/');
      }
    } else {
      setError(result.payload || 'Login failed');
    }
  };

  return (
    <Box>
      <Typography variant="h2" gutterBottom>Welcome back</Typography>
      <Typography variant="body2" color="text.secondary" sx={{ mb: 4 }}>
        Sign in to your Medis HMS account
      </Typography>

      <ErrorMessage message={error} />

      <Box component="form" onSubmit={handleSubmit} sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
        <TextField
          label="Email"
          type="email"
          value={form.email}
          onChange={(e) => setForm({ ...form, email: e.target.value })}
          required
          fullWidth
          autoComplete="email"
          autoFocus
        />
        <TextField
          label="Password"
          type="password"
          value={form.password}
          onChange={(e) => setForm({ ...form, password: e.target.value })}
          required
          fullWidth
          autoComplete="current-password"
        />
        <Button
          type="submit"
          variant="contained"
          fullWidth
          size="large"
          disabled={loading}
          sx={{ mt: 1, py: 1.5 }}
        >
          {loading ? <CircularProgress size={22} sx={{ color: '#fff' }} /> : 'Sign in'}
        </Button>
        <Typography variant="body2" sx={{ mt: 2, textAlign: 'center' }}>
          Don't have an account?{' '}
          <Link component={RouterLink} to="/register">
            Register here
          </Link>
        </Typography>
      </Box>
    </Box>
  );
}
