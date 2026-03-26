import { useEffect, useState } from 'react';
import {
  Box, Card, Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
  Button, Dialog, DialogTitle, DialogContent, DialogActions, TextField,
  MenuItem, Select, FormControl, InputLabel, IconButton, Tooltip,
  CircularProgress, Avatar, InputAdornment,
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import EditIcon from '@mui/icons-material/Edit';
import SearchIcon from '@mui/icons-material/Search';
import { patientsApi } from '../api/services';
import { PageHeader, EmptyState, ErrorMessage } from '../components/common';
import { useAuth } from '../hooks/useAuth';

const BLOOD_GROUPS = ['A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-'];

export default function PatientsPage() {
  const { isAdmin, isReceptionist } = useAuth();
  const canWrite = isAdmin || isReceptionist;

  const [patients, setPatients] = useState([]);
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(true);
  const [registerOpen, setRegisterOpen] = useState(false);
  const [editOpen, setEditOpen] = useState(false);
  const [selected, setSelected] = useState(null);
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [form, setForm] = useState({ email: '', bloodGroup: 'O+', address: '', emergencyContact: '' });
  const [editForm, setEditForm] = useState({ address: '', emergencyContact: '' });

  const load = async () => {
    setLoading(true);
    try {
      const { data } = await patientsApi.getAll();
      setPatients(data.content ?? data);
    } catch { setPatients([]); }
    setLoading(false);
  };
  useEffect(() => { load(); }, []);

  const filtered = patients.filter(p => {
    if (!search) return true;
    const q = search.toLowerCase();
    return (
      String(p.id).includes(q) ||
      p.email?.toLowerCase().includes(q) ||
      p.name?.toLowerCase().includes(q) ||
      p.bloodGroup?.toLowerCase().includes(q) ||
      p.address?.toLowerCase().includes(q) ||
      p.emergencyContact?.includes(q)
    );
  });

  const handleRegister = async (e) => {
    e.preventDefault(); setSubmitting(true); setError('');
    try {
      await patientsApi.register(form);
      setRegisterOpen(false);
      setForm({ email: '', bloodGroup: 'O+', address: '', emergencyContact: '' });
      load();
    } catch (err) { setError(err.response?.data?.message || 'Failed to register patient'); }
    setSubmitting(false);
  };

  const handleEdit = async (e) => {
    e.preventDefault(); setSubmitting(true); setError('');
    try {
      if (editForm.address !== selected.address)
        await patientsApi.updateAddress(selected.id, editForm.address);
      if (editForm.emergencyContact !== selected.emergencyContact)
        await patientsApi.updateEmergencyContact(selected.id, editForm.emergencyContact);
      setEditOpen(false);
      load();
    } catch (err) { setError(err.response?.data?.message || 'Failed to update'); }
    setSubmitting(false);
  };

  const openEdit = (p) => {
    setSelected(p);
    setEditForm({ address: p.address, emergencyContact: p.emergencyContact });
    setError('');
    setEditOpen(true);
  };

  return (
    <Box>
      <PageHeader
        title="Patients"
        subtitle={`${patients.length} registered patient${patients.length !== 1 ? 's' : ''}`}
        action={canWrite && (
          <Button variant="contained" startIcon={<AddIcon />}
            onClick={() => { setError(''); setRegisterOpen(true); }}>
            Register patient
          </Button>
        )}
      />

      <TextField
        placeholder="Search by name, email, blood group, address..."
        value={search}
        onChange={e => setSearch(e.target.value)}
        size="small"
        sx={{ mb: 2, width: 380 }}
        InputProps={{
          startAdornment: (
            <InputAdornment position="start">
              <SearchIcon fontSize="small" sx={{ color: 'text.secondary' }} />
            </InputAdornment>
          ),
        }}
      />

      <Card>
        <TableContainer>
          {loading
            ? <Box sx={{ py: 6, textAlign: 'center' }}><CircularProgress size={28} color="primary" /></Box>
            : filtered.length === 0
              ? <EmptyState message={search ? 'No patients match your search' : 'No patients registered yet'} />
              : (
                <Table>
                  <TableHead>
                    <TableRow>
                      <TableCell>Patient</TableCell>
                      <TableCell>Blood group</TableCell>
                      <TableCell>Address</TableCell>
                      <TableCell>Emergency contact</TableCell>
                      {canWrite && <TableCell align="right">Actions</TableCell>}
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {filtered.map(p => (
                      <TableRow key={p.id} hover>
                        <TableCell>
                          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
                            <Avatar sx={{ width: 34, height: 34, bgcolor: '#EEEDFE', color: '#3C3489', fontSize: 13, fontWeight: 600 }}>
                              {p.name?.charAt(0).toUpperCase() ?? '?'}
                            </Avatar>
                            <Box>
                              <Box sx={{ fontWeight: 500, fontSize: '0.875rem' }}>{p.name}</Box>
                              <Box sx={{ fontSize: '0.75rem', color: 'text.secondary' }}>Patient #{p.id} · {p.email}</Box>
                            </Box>
                          </Box>
                        </TableCell>
                        <TableCell>
                          <Box sx={{ display: 'inline-block', px: 1.5, py: 0.25, borderRadius: 10, bgcolor: '#FCEBEB', color: '#A32D2D', fontSize: '0.75rem', fontWeight: 600 }}>
                            {p.bloodGroup}
                          </Box>
                        </TableCell>
                        <TableCell sx={{ color: 'text.secondary', maxWidth: 200 }}>{p.address}</TableCell>
                        <TableCell>{p.emergencyContact}</TableCell>
                        {canWrite && (
                          <TableCell align="right">
                            <Tooltip title="Edit">
                              <IconButton size="small" onClick={() => openEdit(p)}>
                                <EditIcon fontSize="small" />
                              </IconButton>
                            </Tooltip>
                          </TableCell>
                        )}
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              )}
        </TableContainer>
      </Card>

      {/* Register dialog */}
      <Dialog open={registerOpen} onClose={() => setRegisterOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Register patient</DialogTitle>
        <DialogContent sx={{ pt: 2 }}>
          <ErrorMessage message={error} />
          <Box component="form" id="pat-form" onSubmit={handleRegister}
            sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 1 }}>
            <TextField
              label="Patient Email"
              type="email"
              value={form.email}
              onChange={e => setForm({ ...form, email: e.target.value })}
              required
              fullWidth
            />
            <FormControl fullWidth size="small">
              <InputLabel>Blood group</InputLabel>
              <Select label="Blood group" value={form.bloodGroup}
                onChange={e => setForm({ ...form, bloodGroup: e.target.value })}>
                {BLOOD_GROUPS.map(bg => <MenuItem key={bg} value={bg}>{bg}</MenuItem>)}
              </Select>
            </FormControl>
            <TextField label="Address" value={form.address}
              onChange={e => setForm({ ...form, address: e.target.value })}
              required fullWidth multiline rows={2} />
            <TextField label="Emergency contact" value={form.emergencyContact}
              onChange={e => setForm({ ...form, emergencyContact: e.target.value })} required fullWidth />
          </Box>
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2 }}>
          <Button onClick={() => setRegisterOpen(false)} variant="outlined" size="small">Cancel</Button>
          <Button type="submit" form="pat-form" variant="contained" size="small" disabled={submitting}>
            {submitting ? <CircularProgress size={18} sx={{ color: '#fff' }} /> : 'Register'}
          </Button>
        </DialogActions>
      </Dialog>

      {/* Edit dialog */}
      <Dialog open={editOpen} onClose={() => setEditOpen(false)} maxWidth="xs" fullWidth>
        <DialogTitle>Edit patient #{selected?.id}</DialogTitle>
        <DialogContent sx={{ pt: 2 }}>
          <ErrorMessage message={error} />
          <Box component="form" id="edit-pat-form" onSubmit={handleEdit}
            sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 1 }}>
            <TextField label="Address" value={editForm.address}
              onChange={e => setEditForm({ ...editForm, address: e.target.value })}
              required fullWidth multiline rows={2} />
            <TextField label="Emergency contact" value={editForm.emergencyContact}
              onChange={e => setEditForm({ ...editForm, emergencyContact: e.target.value })} required fullWidth />
          </Box>
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2 }}>
          <Button onClick={() => setEditOpen(false)} variant="outlined" size="small">Cancel</Button>
          <Button type="submit" form="edit-pat-form" variant="contained" size="small" disabled={submitting}>
            {submitting ? <CircularProgress size={18} sx={{ color: '#fff' }} /> : 'Save'}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}