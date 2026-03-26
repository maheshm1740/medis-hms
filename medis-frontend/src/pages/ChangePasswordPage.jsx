import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import { Box, Typography, TextField, Button, CircularProgress } from '@mui/material';
import { usersApi } from '../api/services';
import { setUser } from '../app/slices/authSlice';
import { useAuth } from '../hooks/useAuth';
import { ErrorMessage } from '../components/common';

export default function ChangePasswordPage() {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const { user } = useAuth();
  const [form, setForm] = useState({ oldPassword: '', newPassword: '', confirm: '' });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (form.newPassword !== form.confirm) { setError('Passwords do not match'); return; }
    if (form.newPassword.length < 6) { setError('Password must be at least 6 characters'); return; }
    setLoading(true);
    setError('');
    try {
      const userId = user?.id;
      await usersApi.updatePassword(userId, { oldPassword: form.oldPassword, newPassword: form.newPassword });
      dispatch(setUser({ ...user, passwordChanged: true }));
      navigate('/');
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to change password');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box>
      <Typography variant="h2" gutterBottom>Set your password</Typography>
      <Typography variant="body2" color="text.secondary" sx={{ mb: 4 }}>
        Your account requires a password change before you can continue.
      </Typography>

      <ErrorMessage message={error} />

      <Box component="form" onSubmit={handleSubmit} sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
        <TextField label="Current password" type="password" value={form.oldPassword}
          onChange={(e) => setForm({ ...form, oldPassword: e.target.value })} required fullWidth />
        <TextField label="New password" type="password" value={form.newPassword}
          onChange={(e) => setForm({ ...form, newPassword: e.target.value })} required fullWidth />
        <TextField label="Confirm new password" type="password" value={form.confirm}
          onChange={(e) => setForm({ ...form, confirm: e.target.value })} required fullWidth />
        <Button type="submit" variant="contained" fullWidth size="large" disabled={loading} sx={{ mt: 1, py: 1.5 }}>
          {loading ? <CircularProgress size={22} sx={{ color: '#fff' }} /> : 'Update password'}
        </Button>
      </Box>
    </Box>
  );
}
