import { useEffect, useState } from 'react';
import {
  Box, Card, Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
  Button, Dialog, DialogTitle, DialogContent, DialogActions, TextField,
  IconButton, Tooltip, CircularProgress, InputAdornment,
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import EditIcon from '@mui/icons-material/Edit';
import SearchIcon from '@mui/icons-material/Search';
import { medicinesApi } from '../api/services';
import { PageHeader, EmptyState, ErrorMessage } from '../components/common';
import { useAuth } from '../hooks/useAuth';

export default function MedicinesPage() {

  const { isAdmin, isPharmacist } = useAuth();
  const canWrite = isAdmin || isPharmacist;

  const [medicines, setMedicines] = useState([]);
  const [loading, setLoading] = useState(false);
  const [search, setSearch] = useState('');

  const [addOpen, setAddOpen] = useState(false);
  const [editOpen, setEditOpen] = useState(false);
  const [selected, setSelected] = useState(null);

  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const [form, setForm] = useState({
    name: '',
    manufacturer: '',
    price: ''
  });

  const [editForm, setEditForm] = useState({
    price: '',
    manufacturer: ''
  });

  // 🚀 Fetch medicines (supports search)
  const fetchMedicines = async (search = '') => {
    setLoading(true);

    try {
      const { data } = await medicinesApi.getAll({ search });
      setMedicines(data);
    } catch {
      setMedicines([]);
    }

    setLoading(false);
  };

  // 🚀 Initial load
  useEffect(() => {
    fetchMedicines('');
  }, []);

  // 🚀 Dynamic search (debounced)
  useEffect(() => {
    const delay = setTimeout(() => {
      fetchMedicines(search);
    }, 400);

    return () => clearTimeout(delay);
  }, [search]);

  // 🚀 Add medicine
  const handleAdd = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setError('');

    try {
      await medicinesApi.add({
        name: form.name,
        manufacturer: form.manufacturer,
        price: Number(form.price)
      });

      setAddOpen(false);
      setForm({ name: '', manufacturer: '', price: '' });

      fetchMedicines(search);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to add medicine');
    }

    setSubmitting(false);
  };

  // 🚀 Edit medicine
  const handleEdit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setError('');

    try {
      if (editForm.price !== String(selected.price)) {
        await medicinesApi.updatePrice(selected.id, editForm.price);
      }

      if (editForm.manufacturer !== selected.manufacturer) {
        await medicinesApi.updateManufacturer(selected.id, editForm.manufacturer);
      }

      setEditOpen(false);
      fetchMedicines(search);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to update');
    }

    setSubmitting(false);
  };

  const openEdit = (m) => {
    setSelected(m);
    setEditForm({
      price: String(m.price),
      manufacturer: m.manufacturer
    });
    setError('');
    setEditOpen(true);
  };

  return (
    <Box>

      <PageHeader
        title="Medicines"
        subtitle="Medicine catalogue and pricing"
        action={
          canWrite && (
            <Button
              variant="contained"
              startIcon={<AddIcon />}
              onClick={() => {
                setError('');
                setAddOpen(true);
              }}
            >
              Add medicine
            </Button>
          )
        }
      />

      {/* 🔍 Dynamic Search */}
      <Box sx={{ display: 'flex', gap: 1.5, mb: 2 }}>
        <TextField
          placeholder="Search by name or manufacturer"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          size="small"
          sx={{ width: 300 }}
          InputProps={{
            startAdornment: (
              <InputAdornment position="start">
                <SearchIcon fontSize="small" sx={{ color: 'text.secondary' }} />
              </InputAdornment>
            ),
            endAdornment: loading && (
              <CircularProgress size={16} />
            )
          }}
        />
      </Box>

      {/* 📊 Table */}
      <Card>
        <TableContainer>

          {loading ? (
            <Box sx={{ py: 6, textAlign: 'center' }}>
              <CircularProgress size={28} />
            </Box>
          ) : medicines.length === 0 ? (
            <EmptyState message="No medicines found" />
          ) : (
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>ID</TableCell>
                  <TableCell>Name</TableCell>
                  <TableCell>Manufacturer</TableCell>
                  <TableCell>Price</TableCell>
                  {canWrite && <TableCell align="right">Actions</TableCell>}
                </TableRow>
              </TableHead>

              <TableBody>
                {medicines.map((m) => (
                  <TableRow key={m.id} hover>

                    <TableCell sx={{ color: 'text.secondary' }}>
                      #{m.id}
                    </TableCell>

                    <TableCell sx={{ fontWeight: 500 }}>
                      {m.name}
                    </TableCell>

                    <TableCell sx={{ color: 'text.secondary' }}>
                      {m.manufacturer}
                    </TableCell>

                    <TableCell sx={{ fontWeight: 500, color: 'primary.dark' }}>
                      ₹{Number(m.price).toFixed(2)}
                    </TableCell>

                    {canWrite && (
                      <TableCell align="right">
                        <Tooltip title="Edit">
                          <IconButton size="small" onClick={() => openEdit(m)}>
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

      {/* ➕ Add Dialog */}
      <Dialog open={addOpen} onClose={() => setAddOpen(false)} maxWidth="xs" fullWidth>
        <DialogTitle>Add medicine</DialogTitle>

        <DialogContent sx={{ pt: 2 }}>
          <ErrorMessage message={error} />

          <Box
            component="form"
            onSubmit={handleAdd}
            sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 1 }}
          >
            <TextField label="Name" value={form.name} onChange={e => setForm({ ...form, name: e.target.value })} required />
            <TextField label="Manufacturer" value={form.manufacturer} onChange={e => setForm({ ...form, manufacturer: e.target.value })} required />
            <TextField label="Price" type="number" value={form.price} onChange={e => setForm({ ...form, price: e.target.value })} required />
          </Box>
        </DialogContent>

        <DialogActions>
          <Button onClick={() => setAddOpen(false)}>Cancel</Button>
          <Button type="submit" onClick={handleAdd} disabled={submitting}>
            {submitting ? <CircularProgress size={18} /> : 'Add'}
          </Button>
        </DialogActions>
      </Dialog>

      {/* ✏️ Edit Dialog */}
      <Dialog open={editOpen} onClose={() => setEditOpen(false)} maxWidth="xs" fullWidth>
        <DialogTitle>Edit — {selected?.name}</DialogTitle>

        <DialogContent sx={{ pt: 2 }}>
          <ErrorMessage message={error} />

          <Box
            component="form"
            onSubmit={handleEdit}
            sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 1 }}
          >
            <TextField label="Manufacturer" value={editForm.manufacturer} onChange={e => setEditForm({ ...editForm, manufacturer: e.target.value })} required />
            <TextField label="Price" type="number" value={editForm.price} onChange={e => setEditForm({ ...editForm, price: e.target.value })} required />
          </Box>
        </DialogContent>

        <DialogActions>
          <Button onClick={() => setEditOpen(false)}>Cancel</Button>
          <Button type="submit" onClick={handleEdit} disabled={submitting}>
            {submitting ? <CircularProgress size={18} /> : 'Save'}
          </Button>
        </DialogActions>
      </Dialog>

    </Box>
  );
}