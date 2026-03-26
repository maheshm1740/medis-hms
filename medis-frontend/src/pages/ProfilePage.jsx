import { useEffect, useState } from 'react';
import { Box, Card, CardContent, Typography, Divider, TextField, Button, CircularProgress, Alert, Avatar, Grid } from '@mui/material';
import { doctorsApi, patientsApi, usersApi } from '../api/services';
import { useAuth } from '../hooks/useAuth';
import { RoleBadge, PageHeader } from '../components/common';

export default function ProfilePage() {
  const { user, isDoctor, isPatient } = useAuth();
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [pwForm, setPwForm] = useState({ oldPassword: '', newPassword: '', confirm: '' });
  const [pwLoading, setPwLoading] = useState(false);
  const [pwError, setPwError] = useState('');
  const [pwSuccess, setPwSuccess] = useState(false);

  useEffect(() => {
    const load = async () => {
      try {
        if (isDoctor) { const { data } = await doctorsApi.getMe(); setProfile(data); }
        else if (isPatient) { const { data } = await patientsApi.getMe(); setProfile(data); }
      } catch {}
      setLoading(false);
    };
    load();
  }, []);

  const handlePw = async (e) => {
    e.preventDefault();
    if (pwForm.newPassword !== pwForm.confirm) { setPwError('Passwords do not match'); return; }
    setPwLoading(true); setPwError(''); setPwSuccess(false);
    try {
      await usersApi.updatePassword(user?.id, { oldPassword: pwForm.oldPassword, newPassword: pwForm.newPassword });
      setPwSuccess(true); setPwForm({ oldPassword: '', newPassword: '', confirm: '' });
    } catch (e) { setPwError(e.response?.data?.message || 'Password update failed'); }
    setPwLoading(false);
  };

  const initials = user?.email?.slice(0, 2).toUpperCase() || 'US';

  return (
    <Box>
      <PageHeader title="My profile" subtitle="View and manage your account" />
      <Grid container spacing={2}>
        <Grid item xs={12} md={5}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 2 }}>
                <Avatar sx={{ width: 56, height: 56, bgcolor: 'primary.main', fontSize: 20, fontWeight: 600 }}>{initials}</Avatar>
                <Box>
                  <Typography variant="h3">{user?.email}</Typography>
                  <RoleBadge role={user?.role} />
                </Box>
              </Box>
              <Divider sx={{ mb: 2 }} />
              {loading ? <CircularProgress size={20} /> : profile && (
                <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1.5 }}>
                  {isDoctor && (
                    <>
                      <Row label="Department" value={profile.department} />
                      <Row label="Specialization" value={profile.specialization?.join(', ')} />
                      <Row label="License" value={profile.licenseNumber} />
                      <Row label="Experience" value={`${profile.experienceYears} years`} />
                    </>
                  )}
                  {isPatient && (
                    <>
                      <Row label="Blood group" value={profile.bloodGroup} />
                      <Row label="Address" value={profile.address} />
                      <Row label="Emergency contact" value={profile.emergencyContact} />
                    </>
                  )}
                </Box>
              )}
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={7}>
          <Card>
            <CardContent>
              <Typography variant="h4" gutterBottom>Change password</Typography>
              <Divider sx={{ mb: 2 }} />
              {pwError && <Alert severity="error" sx={{ mb: 2 }}>{pwError}</Alert>}
              {pwSuccess && <Alert severity="success" sx={{ mb: 2 }}>Password updated successfully</Alert>}
              <Box component="form" onSubmit={handlePw} sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                <TextField label="Current password" type="password" value={pwForm.oldPassword} onChange={e => setPwForm({ ...pwForm, oldPassword: e.target.value })} required fullWidth />
                <TextField label="New password" type="password" value={pwForm.newPassword} onChange={e => setPwForm({ ...pwForm, newPassword: e.target.value })} required fullWidth />
                <TextField label="Confirm new password" type="password" value={pwForm.confirm} onChange={e => setPwForm({ ...pwForm, confirm: e.target.value })} required fullWidth />
                <Button type="submit" variant="contained" disabled={pwLoading} sx={{ alignSelf: 'flex-start' }}>
                  {pwLoading ? <CircularProgress size={18} sx={{ color: '#fff' }} /> : 'Update password'}
                </Button>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
}

function Row({ label, value }) {
  return (
    <Box sx={{ display: 'flex', justifyContent: 'space-between', gap: 2 }}>
      <Typography variant="caption" color="text.secondary" sx={{ minWidth: 120 }}>{label}</Typography>
      <Typography variant="body2" fontWeight={500} sx={{ textAlign: 'right' }}>{value || '—'}</Typography>
    </Box>
  );
}
