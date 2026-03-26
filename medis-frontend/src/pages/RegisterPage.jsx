import { useState } from 'react';
import { useNavigate, Link as RouterLink } from 'react-router-dom';
import {
  Box,
  Typography,
  TextField,
  Button,
  CircularProgress,
  Link
} from '@mui/material';
import { usersApi } from '../api/services';
import { ErrorMessage } from '../components/common';

export default function RegisterPage() {
  const navigate = useNavigate();

  const [form, setForm] = useState({
    name: '',
    email: '',
    phone: '',
    password: ''
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      await usersApi.register(form);

      // After successful registration → redirect to login
      navigate('/login');
    } catch (err) {
      setError(err.response?.data?.message || 'Registration failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box>
      <Typography variant="h2" gutterBottom>
        Create Account
      </Typography>

      <Typography variant="body2" color="text.secondary" sx={{ mb: 4 }}>
        Register as a patient in Medis HMS
      </Typography>

      <ErrorMessage message={error} />

      <Box
        component="form"
        onSubmit={handleSubmit}
        sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}
      >
        <TextField
          label="Full Name"
          value={form.name}
          onChange={(e) => setForm({ ...form, name: e.target.value })}
          required
          fullWidth
        />

        <TextField
          label="Email"
          type="email"
          value={form.email}
          onChange={(e) => setForm({ ...form, email: e.target.value })}
          required
          fullWidth
        />

        <TextField
          label="Phone"
          value={form.phone}
          onChange={(e) => setForm({ ...form, phone: e.target.value })}
          required
          fullWidth
        />

        <TextField
          label="Password"
          type="password"
          value={form.password}
          onChange={(e) => setForm({ ...form, password: e.target.value })}
          required
          fullWidth
        />

        <Button
          type="submit"
          variant="contained"
          fullWidth
          size="large"
          disabled={loading}
          sx={{ mt: 1, py: 1.5 }}
        >
          {loading ? (
            <CircularProgress size={22} sx={{ color: '#fff' }} />
          ) : (
            'Register'
          )}
        </Button>
      </Box>

      {/* 🔥 Back to login */}
      <Typography variant="body2" sx={{ mt: 2, textAlign: 'center' }}>
        Already have an account?{' '}
        <Link component={RouterLink} to="/login">
          Sign in
        </Link>
      </Typography>
    </Box>
  );
}