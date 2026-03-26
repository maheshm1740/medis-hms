import { useEffect, useState } from 'react';
import {
  Box, Card, Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
  Button, Dialog, DialogTitle, DialogContent, DialogActions, TextField,
  IconButton, Tooltip, Chip, CircularProgress, Avatar,
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import EditIcon from '@mui/icons-material/Edit';
import { doctorsApi } from '../api/services';
import { PageHeader, EmptyState, ErrorMessage } from '../components/common';
import { useAuth } from '../hooks/useAuth';

export default function DoctorsPage() {
  const { isAdmin } = useAuth();
  const [doctors, setDoctors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [registerOpen, setRegisterOpen] = useState(false);
  const [editOpen, setEditOpen] = useState(false);
  const [selected, setSelected] = useState(null);
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [form, setForm] = useState({ email: '', specialization: '', department: '', licenseNumber: '', experienceYears: '' });
  const [editForm, setEditForm] = useState({ department: '', specialization: '' });

  const load = async () => {
    setLoading(true);
    try {
      const { data } = await doctorsApi.getAll();
      setDoctors(data);
    } catch { setDoctors([]); }
    setLoading(false);
  };
  useEffect(() => { load(); }, []);

  const handleRegister = async (e) => {
    e.preventDefault(); setSubmitting(true); setError('');
    try {
      await doctorsApi.register({
        email: form.email,                  // ← was userId: Number(form.userId)
        specialization: form.specialization.split(',').map(s => s.trim()).filter(Boolean),
        department: form.department,
        licenseNumber: form.licenseNumber,
        experienceYears: Number(form.experienceYears),
      });
      setRegisterOpen(false);
      setForm({ email: '', specialization: '', department: '', licenseNumber: '', experienceYears: '' });
      load();
    } catch (err) { setError(err.response?.data?.message || 'Failed to register doctor'); }
    setSubmitting(false);
  };

  const handleEdit = async (e) => {
    e.preventDefault(); setSubmitting(true); setError('');
    try {
      if (editForm.department !== selected.department)
        await doctorsApi.updateDepartment(selected.id, editForm.department);
      const newSpecs = editForm.specialization.split(',').map(s => s.trim()).filter(Boolean);
      await doctorsApi.updateSpecialization(selected.id, newSpecs);
      setEditOpen(false);
      load();
    } catch (err) { setError(err.response?.data?.message || 'Failed to update'); }
    setSubmitting(false);
  };

  const openEdit = (d) => {
    setSelected(d);
    setEditForm({ department: d.department, specialization: d.specialization.join(', ') });
    setError('');
    setEditOpen(true);
  };

  return (
    <Box>
      <PageHeader
        title="Doctors"
        subtitle="Registered doctor profiles"
        action={isAdmin && (
          <Button variant="contained" startIcon={<AddIcon />}
            onClick={() => { setError(''); setRegisterOpen(true); }}>
            Register doctor
          </Button>
        )}
      />
      <Card>
        <TableContainer>
          {loading
            ? <Box sx={{ py: 6, textAlign: 'center' }}><CircularProgress size={28} color="primary" /></Box>
            : doctors.length === 0
              ? <EmptyState message="No doctors registered" />
              : (
                <Table>
                  <TableHead>
                    <TableRow>
                      <TableCell>Doctor</TableCell>
                      <TableCell>Department</TableCell>
                      <TableCell>Specializations</TableCell>
                      <TableCell>License</TableCell>
                      <TableCell>Experience</TableCell>
                      <TableCell align="right">Actions</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {doctors.map(d => (
                      <TableRow key={d.id} hover>
                        <TableCell>
                          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
                            <Avatar sx={{ width: 34, height: 34, bgcolor: 'primary.light', fontSize: 13, fontWeight: 600 }}>
                              {d.name?.charAt(0).toUpperCase() ?? '?'}
                            </Avatar>
                            <Box>
                              <Box sx={{ fontWeight: 500, fontSize: '0.875rem' }}>{d.name}</Box>
                              <Box sx={{ fontSize: '0.75rem', color: 'text.secondary' }}>
                                Doctor #{d.id} · {d.email}   {/* ← was User #{d.userId} */}
                              </Box>
                            </Box>
                          </Box>
                        </TableCell>
                        <TableCell>{d.department}</TableCell>
                        <TableCell>
                          <Box sx={{ display: 'flex', gap: 0.5, flexWrap: 'wrap' }}>
                            {d.specialization.map(s => (
                              <Chip key={s} label={s} size="small" sx={{ fontSize: '0.7rem', height: 20 }} />
                            ))}
                          </Box>
                        </TableCell>
                        <TableCell sx={{ color: 'text.secondary', fontFamily: 'monospace', fontSize: '0.8rem' }}>{d.licenseNumber}</TableCell>
                        <TableCell>
                          {d.experienceYears} yrs
                          {d.experiencedDoctor && (
                            <Chip label="Senior" size="small" color="primary" sx={{ ml: 1, height: 18, fontSize: '0.65rem' }} />
                          )}
                        </TableCell>
                        <TableCell align="right">
                          <Tooltip title="Edit">
                            <IconButton size="small" onClick={() => openEdit(d)}>
                              <EditIcon fontSize="small" />
                            </IconButton>
                          </Tooltip>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              )}
        </TableContainer>
      </Card>

      {/* Register dialog */}
      <Dialog open={registerOpen} onClose={() => setRegisterOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Register doctor</DialogTitle>
        <DialogContent sx={{ pt: 2 }}>
          <ErrorMessage message={error} />
          <Box component="form" id="reg-form" onSubmit={handleRegister}
            sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 1 }}>
            <TextField
              label="User Email"
              type="email"
              value={form.email}
              onChange={e => setForm({ ...form, email: e.target.value })}
              required
              fullWidth
            />
            <TextField label="Specializations (comma-separated)" value={form.specialization}
              onChange={e => setForm({ ...form, specialization: e.target.value })}
              required fullWidth placeholder="Cardiology, Neurology" />
            <TextField label="Department" value={form.department}
              onChange={e => setForm({ ...form, department: e.target.value })} required fullWidth />
            <TextField label="License number" value={form.licenseNumber}
              onChange={e => setForm({ ...form, licenseNumber: e.target.value })} required fullWidth />
            <TextField label="Years of experience" value={form.experienceYears}
              onChange={e => setForm({ ...form, experienceYears: e.target.value })}
              required fullWidth type="number" inputProps={{ min: 0 }} />
          </Box>
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2 }}>
          <Button onClick={() => setRegisterOpen(false)} variant="outlined" size="small">Cancel</Button>
          <Button type="submit" form="reg-form" variant="contained" size="small" disabled={submitting}>
            {submitting ? <CircularProgress size={18} sx={{ color: '#fff' }} /> : 'Register'}
          </Button>
        </DialogActions>
      </Dialog>

      {/* Edit dialog */}
      <Dialog open={editOpen} onClose={() => setEditOpen(false)} maxWidth="xs" fullWidth>
        <DialogTitle>Edit doctor #{selected?.id}</DialogTitle>
        <DialogContent sx={{ pt: 2 }}>
          <ErrorMessage message={error} />
          <Box component="form" id="edit-form" onSubmit={handleEdit}
            sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 1 }}>
            <TextField label="Department" value={editForm.department}
              onChange={e => setEditForm({ ...editForm, department: e.target.value })} required fullWidth />
            <TextField label="Specializations (comma-separated)" value={editForm.specialization}
              onChange={e => setEditForm({ ...editForm, specialization: e.target.value })} required fullWidth />
          </Box>
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2 }}>
          <Button onClick={() => setEditOpen(false)} variant="outlined" size="small">Cancel</Button>
          <Button type="submit" form="edit-form" variant="contained" size="small" disabled={submitting}>
            {submitting ? <CircularProgress size={18} sx={{ color: '#fff' }} /> : 'Save'}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}