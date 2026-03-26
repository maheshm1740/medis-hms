import { useEffect, useState } from 'react';
import {
  Box, Card, Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
  Button, Dialog, DialogTitle, DialogContent, DialogActions, TextField,
  IconButton, Tooltip, CircularProgress, Alert, Chip, LinearProgress,
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import AddCircleOutlineIcon from '@mui/icons-material/AddCircleOutline';
import RemoveCircleOutlineIcon from '@mui/icons-material/RemoveCircleOutline';
import { inventoryApi, medicinesApi } from '../api/services';
import { PageHeader, EmptyState } from '../components/common';

export default function InventoryPage() {
  const [medicines, setMedicines] = useState([]);
  const [inventory, setInventory] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [createOpen, setCreateOpen] = useState(false);
  const [stockOpen, setStockOpen] = useState(false);
  const [stockAction, setStockAction] = useState('add');
  const [selectedMed, setSelectedMed] = useState(null);
  const [submitting, setSubmitting] = useState(false);
  const [formError, setFormError] = useState('');
  const [createForm, setCreateForm] = useState({ medicineId: '', stockQuantity: '', reorderLevel: '' });
  const [quantity, setQuantity] = useState('');

  const loadAll = async () => {
    setLoading(true);
    try {
      const { data: meds } = await medicinesApi.getAll();
      setMedicines(meds);
      const inv = {};
      await Promise.allSettled(meds.map(async (m) => {
        try { const { data } = await inventoryApi.getByMedicine(m.id); inv[m.id] = data; } catch {}
      }));
      setInventory(inv);
    } catch (e) { setError('Failed to load inventory'); }
    setLoading(false);
  };

  useEffect(() => { loadAll(); }, []);

  const handleCreate = async () => {
    setSubmitting(true); setFormError('');
    try {
      await inventoryApi.create({ medicineId: Number(createForm.medicineId), stockQuantity: Number(createForm.stockQuantity), reorderLevel: Number(createForm.reorderLevel) });
      setCreateOpen(false); setCreateForm({ medicineId: '', stockQuantity: '', reorderLevel: '' }); loadAll();
    } catch (e) { setFormError(e.response?.data?.message || 'Failed to create inventory'); }
    setSubmitting(false);
  };

  const handleStock = async () => {
    setSubmitting(true); setFormError('');
    try {
      if (stockAction === 'add') await inventoryApi.addStock(selectedMed.id, Number(quantity));
      else await inventoryApi.deductStock(selectedMed.id, Number(quantity));
      setStockOpen(false); setQuantity(''); loadAll();
    } catch (e) { setFormError(e.response?.data?.message || 'Stock update failed'); }
    setSubmitting(false);
  };

  const openStock = (med, action) => { setSelectedMed(med); setStockAction(action); setQuantity(''); setFormError(''); setStockOpen(true); };

  const stockPct = (inv) => inv.reorderLevel === 0 ? 100 : Math.min(100, Math.round((inv.stockQuantity / (inv.reorderLevel * 3)) * 100));

  return (
    <Box>
      <PageHeader title="Inventory" subtitle="Medicine stock management"
        action={<Button variant="contained" startIcon={<AddIcon />} onClick={() => setCreateOpen(true)}>Create entry</Button>}
      />
      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
      <Card>
        <TableContainer>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Medicine</TableCell><TableCell>Stock</TableCell>
                <TableCell>Reorder level</TableCell><TableCell>Status</TableCell>
                <TableCell align="right">Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {loading ? (
                <TableRow><TableCell colSpan={5} align="center"><CircularProgress size={24} sx={{ my: 2 }} /></TableCell></TableRow>
              ) : medicines.filter(m => inventory[m.id]).length === 0 ? (
                <TableRow><TableCell colSpan={5}><EmptyState message="No inventory entries yet" /></TableCell></TableRow>
              ) : medicines.filter(m => inventory[m.id]).map((m) => {
                const inv = inventory[m.id];
                const low = inv.stockQuantity <= inv.reorderLevel;
                return (
                  <TableRow key={m.id} hover>
                    <TableCell>
                      <Box sx={{ fontWeight: 500 }}>{m.name}</Box>
                      <Box sx={{ fontSize: '0.75rem', color: 'text.secondary' }}>{m.manufacturer}</Box>
                    </TableCell>
                    <TableCell>
                      <Box sx={{ fontWeight: 600, fontSize: '1.1rem', fontFamily: 'monospace' }}>{inv.stockQuantity}</Box>
                      <LinearProgress variant="determinate" value={stockPct(inv)}
                        sx={{ mt: 0.5, height: 4, borderRadius: 2, bgcolor: 'grey.100',
                          '& .MuiLinearProgress-bar': { bgcolor: low ? '#A32D2D' : '#0F6E56' } }} />
                    </TableCell>
                    <TableCell>{inv.reorderLevel}</TableCell>
                    <TableCell>
                      {low ? <Chip label="Low stock" size="small" sx={{ height: 20, fontSize: '0.68rem', bgcolor: '#FCEBEB', color: '#A32D2D', fontWeight: 600 }} />
                           : <Chip label="In stock" size="small" sx={{ height: 20, fontSize: '0.68rem', bgcolor: '#E1F5EE', color: '#0F6E56', fontWeight: 600 }} />}
                    </TableCell>
                    <TableCell align="right">
                      <Box sx={{ display: 'flex', gap: 0.5, justifyContent: 'flex-end' }}>
                        <Tooltip title="Add stock"><IconButton size="small" color="success" onClick={() => openStock(m, 'add')}><AddCircleOutlineIcon fontSize="small" /></IconButton></Tooltip>
                        <Tooltip title="Deduct stock"><IconButton size="small" color="error" onClick={() => openStock(m, 'deduct')}><RemoveCircleOutlineIcon fontSize="small" /></IconButton></Tooltip>
                      </Box>
                    </TableCell>
                  </TableRow>
                );
              })}
            </TableBody>
          </Table>
        </TableContainer>
      </Card>

      <Dialog open={createOpen} onClose={() => setCreateOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Create inventory entry</DialogTitle>
        <DialogContent sx={{ display: 'flex', flexDirection: 'column', gap: 2, pt: 2 }}>
          {formError && <Alert severity="error">{formError}</Alert>}
          <TextField label="Medicine ID" type="number" value={createForm.medicineId} onChange={e => setCreateForm({ ...createForm, medicineId: e.target.value })} required fullWidth />
          <TextField label="Initial stock quantity" type="number" value={createForm.stockQuantity} onChange={e => setCreateForm({ ...createForm, stockQuantity: e.target.value })} required fullWidth />
          <TextField label="Reorder level" type="number" value={createForm.reorderLevel} onChange={e => setCreateForm({ ...createForm, reorderLevel: e.target.value })} required fullWidth helperText="Alert when stock drops to or below this level" />
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2 }}>
          <Button onClick={() => setCreateOpen(false)}>Cancel</Button>
          <Button variant="contained" onClick={handleCreate} disabled={submitting}>{submitting ? <CircularProgress size={18} sx={{ color: '#fff' }} /> : 'Create'}</Button>
        </DialogActions>
      </Dialog>

      <Dialog open={stockOpen} onClose={() => setStockOpen(false)} maxWidth="xs" fullWidth>
        <DialogTitle>{stockAction === 'add' ? 'Add stock' : 'Deduct stock'} — {selectedMed?.name}</DialogTitle>
        <DialogContent sx={{ pt: 2 }}>
          {formError && <Alert severity="error" sx={{ mb: 2 }}>{formError}</Alert>}
          <TextField label="Quantity" type="number" value={quantity} onChange={e => setQuantity(e.target.value)} required fullWidth autoFocus />
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2 }}>
          <Button onClick={() => setStockOpen(false)}>Cancel</Button>
          <Button variant="contained" color={stockAction === 'add' ? 'success' : 'error'} onClick={handleStock} disabled={submitting}>
            {submitting ? <CircularProgress size={18} sx={{ color: '#fff' }} /> : stockAction === 'add' ? 'Add' : 'Deduct'}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
