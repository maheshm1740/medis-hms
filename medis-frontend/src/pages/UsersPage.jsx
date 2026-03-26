import { useState, useEffect } from 'react';
import {
  Box, Card, Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
  Button, Dialog, DialogTitle, DialogContent, DialogActions, TextField, MenuItem,
  CircularProgress, Alert, Avatar, TablePagination
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import { usersApi } from '../api/services';
import { PageHeader, RoleBadge, EmptyState } from '../components/common';

const ROLES = ['DOCTOR', 'PHARMACIST', 'RECEPTIONIST', 'ADMIN'];

export default function UsersPage() {

  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [totalElements, setTotalElements] = useState(0);

  const [users, setUsers] = useState([]);
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const [open, setOpen] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [formError, setFormError] = useState('');
  const [form, setForm] = useState({ name: '', email: '', password: '', phone: '', role: '' });

  // 🚀 Fetch Users
  const fetchUsers = async (search = '', page = 0, size = 10) => {
    setLoading(true);
    setError('');

    try {
      const { data } = await usersApi.getAll({ search, page, size });

      setUsers(data.content);
      setTotalElements(data.totalElements);
    } catch (e) {
      setError('Failed to fetch users');
      setUsers([]);
    }

    setLoading(false);
  };

  // 🚀 Initial load
  useEffect(() => {
    fetchUsers('', 0, rowsPerPage);
  }, []);

  // 🚀 Dynamic search (debounced)
  useEffect(() => {
    const delay = setTimeout(() => {
      fetchUsers(search, 0, rowsPerPage);
      setPage(0);
    }, 400);

    return () => clearTimeout(delay);
  }, [search]);

  // 🚀 Create user
  const handleCreate = async () => {
    setSubmitting(true);
    setFormError('');

    try {
      const { data } = await usersApi.createStaff(form);
      setUsers(prev => [data, ...prev]);
      setOpen(false);
      setForm({ name: '', email: '', password: '', phone: '', role: '' });
    } catch (e) {
      setFormError(e.response?.data?.message || 'Failed to create user');
    }

    setSubmitting(false);
  };

  const initials = (name) => name ? name.slice(0, 2).toUpperCase() : 'US';

  return (
    <Box>

      <PageHeader
        title="Users"
        subtitle="Admin user management"
        action={
          <Button variant="contained" startIcon={<AddIcon />} onClick={() => setOpen(true)}>
            Create staff account
          </Button>
        }
      />

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      {/* 🔍 Search */}
      <Box sx={{ display: 'flex', gap: 1, mb: 2 }}>
        <TextField
          size="small"
          label="Search by name or email"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          sx={{ width: 300 }}
        />
      </Box>

      {/* 📊 Table */}
      <Card>
        <TableContainer>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>ID</TableCell>
                <TableCell>User</TableCell>
                <TableCell>Email</TableCell>
                <TableCell>Phone</TableCell>
                <TableCell>Role</TableCell>
                <TableCell>Password status</TableCell>
              </TableRow>
            </TableHead>

            <TableBody>
              {loading ? (
                <TableRow>
                  <TableCell colSpan={5} align="center">
                    <CircularProgress size={24} sx={{ my: 2 }} />
                  </TableCell>
                </TableRow>
              ) : users.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={5}>
                    <EmptyState message="No users found" />
                  </TableCell>
                </TableRow>
              ) : (
                users.map((u) => (
                  <TableRow key={u.id} hover>
                    <TableCell>
                      <Box sx={{ fontWeight: 600, color: 'primary.main' }}>
                        #{u.id}
                      </Box>
                    </TableCell>
                    <TableCell>
                      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
                        <Avatar sx={{ width: 32, height: 32, bgcolor: 'primary.light', fontSize: 12, fontWeight: 600 }}>
                          {initials(u.name)}
                        </Avatar>
                        <Box sx={{ fontWeight: 500 }}>{u.name}</Box>
                      </Box>
                    </TableCell>

                    <TableCell sx={{ color: 'text.secondary' }}>{u.email}</TableCell>
                    <TableCell>{u.phone}</TableCell>
                    <TableCell><RoleBadge role={u.role} /></TableCell>

                    <TableCell>
                      {u.passwordChanged
                        ? <Box sx={{ fontSize: '0.75rem', color: '#3B6D11' }}>Password set</Box>
                        : <Box sx={{ fontSize: '0.75rem', color: '#854F0B' }}>Pending first login</Box>}
                    </TableCell>
                  </TableRow>
                ))
              )}
            </TableBody>
          </Table>
        </TableContainer>

        {/* 🔥 Pagination */}
        <TablePagination
          component="div"
          count={totalElements}
          page={page}
          onPageChange={(e, newPage) => {
            setPage(newPage);
            fetchUsers(search, newPage, rowsPerPage);
          }}
          rowsPerPage={rowsPerPage}
          onRowsPerPageChange={(e) => {
            const size = parseInt(e.target.value, 10);
            setRowsPerPage(size);
            setPage(0);
            fetchUsers(search, 0, size);
          }}
        />
      </Card>

      {/* 🧾 Create User Dialog */}
      <Dialog open={open} onClose={() => setOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Create staff account</DialogTitle>

        <DialogContent sx={{ display: 'flex', flexDirection: 'column', gap: 2, pt: 2 }}>
          {formError && <Alert severity="error">{formError}</Alert>}

          <TextField label="Full name" value={form.name} onChange={e => setForm({ ...form, name: e.target.value })} required fullWidth />
          <TextField label="Email" type="email" value={form.email} onChange={e => setForm({ ...form, email: e.target.value })} required fullWidth />
          <TextField label="Temporary password" type="password" value={form.password} onChange={e => setForm({ ...form, password: e.target.value })} required fullWidth />
          <TextField label="Phone" value={form.phone} onChange={e => setForm({ ...form, phone: e.target.value })} required fullWidth />

          <TextField select label="Role" value={form.role} onChange={e => setForm({ ...form, role: e.target.value })} required fullWidth>
            {ROLES.map(r => <MenuItem key={r} value={r}>{r}</MenuItem>)}
          </TextField>
        </DialogContent>

        <DialogActions>
          <Button onClick={() => setOpen(false)}>Cancel</Button>
          <Button variant="contained" onClick={handleCreate} disabled={submitting}>
            {submitting ? <CircularProgress size={18} sx={{ color: '#fff' }} /> : 'Create account'}
          </Button>
        </DialogActions>
      </Dialog>

    </Box>
  );
}