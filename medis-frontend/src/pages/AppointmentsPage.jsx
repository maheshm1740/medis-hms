import { useEffect, useState } from 'react';
import {
Box, Card, Table, TableBody, TableCell, TableContainer,
TableHead, TableRow, Button, Dialog, DialogTitle,
DialogContent, DialogActions, TextField, MenuItem, Select,
FormControl, InputLabel, IconButton, Tooltip, Tabs, Tab, CircularProgress,
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline';
import CancelOutlinedIcon from '@mui/icons-material/CancelOutlined';
import ScheduleIcon from '@mui/icons-material/Schedule';
import { appointmentsApi, doctorsApi } from '../api/services';
import { PageHeader, StatusBadge, ConfirmDialog, EmptyState, ErrorMessage } from '../components/common';
import { useAuth } from '../hooks/useAuth';

const TABS = ['SCHEDULED', 'COMPLETED', 'CANCELLED'];

export default function AppointmentsPage() {
const { isAdmin, isReceptionist, isPatient, isDoctor } = useAuth();

const canManage = isAdmin || isReceptionist || isDoctor;
const canBook = isAdmin || isReceptionist || isPatient;

const [tab, setTab] = useState(0);
const [appointments, setAppointments] = useState([]);
const [loading, setLoading] = useState(true);
const [doctors, setDoctors] = useState([]);
const [bookOpen, setBookOpen] = useState(false);
const [rescheduleOpen, setRescheduleOpen] = useState(false);
const [selectedAppt, setSelectedAppt] = useState(null);
const [confirmAction, setConfirmAction] = useState(null);
const [error, setError] = useState('');
const [form, setForm] = useState({
patientId: '',
doctorId: '',
appointmentTime: '',
notes: ''
});
const [newTime, setNewTime] = useState('');
const [submitting, setSubmitting] = useState(false);

// ✅ Fetch appointments (role handled by backend)
const fetchAppointments = async () => {
setLoading(true);
try {
const response = await appointmentsApi.getMyAppointments();
const data = response?.data || [];

  const filtered = data.filter(a => a.status === TABS[tab]);
  setAppointments(filtered);
} catch (err) {
  console.error("Appointments fetch failed:", err);
  setAppointments([]);
}
setLoading(false);

};

useEffect(() => {
fetchAppointments();
}, [tab]);

// Fetch doctors
useEffect(() => {
doctorsApi.getAll()
.then(r => setDoctors(r.data))
.catch(err => console.error("Doctors API failed:", err));
}, []);

const handleBook = async (e) => {
e.preventDefault();
setSubmitting(true);
setError('');

try {
  await appointmentsApi.book({
    ...form,
    patientId: Number(form.patientId),
    doctorId: Number(form.doctorId)
  });

  setBookOpen(false);
  setForm({ patientId: '', doctorId: '', appointmentTime: '', notes: '' });

  fetchAppointments();
} catch (err) {
  console.error(err);
  setError(err.response?.data?.message || 'Failed to book');
}

setSubmitting(false);

};

const handleAction = async () => {
if (!confirmAction) return;

try {
  if (confirmAction.type === 'complete') {
    await appointmentsApi.complete(confirmAction.id);
  }
  if (confirmAction.type === 'cancel') {
    await appointmentsApi.cancel(confirmAction.id);
  }

  setConfirmAction(null);
  fetchAppointments();
} catch (err) {
  console.error(err);
}


};

const handleReschedule = async (e) => {
e.preventDefault();
if (!selectedAppt) return;

setSubmitting(true);

try {
  await appointmentsApi.reschedule(selectedAppt.id, newTime);
  setRescheduleOpen(false);
  fetchAppointments();
} catch (err) {
  console.error(err);
  setError(err.response?.data?.message || 'Failed to reschedule');
}

setSubmitting(false);

};

const fmt = (dt) =>
new Date(dt).toLocaleString('en-IN', {
dateStyle: 'medium',
timeStyle: 'short'
});

console.log(appointments);

return ( <Box>
<PageHeader
title="Appointments"
subtitle="Manage and track all hospital appointments"
action={
canBook && (
<Button
variant="contained"
startIcon={<AddIcon />}
onClick={() => setBookOpen(true)}
>
Book appointment </Button>
)
}
/>

  <Card>
    <Tabs
      value={tab}
      onChange={(_, v) => setTab(v)}
      sx={{ borderBottom: '0.5px solid rgba(0,0,0,0.08)', px: 2 }}
    >
      {TABS.map(t => (
        <Tab key={t} label={t.charAt(0) + t.slice(1).toLowerCase()} />
      ))}
    </Tabs>

    <TableContainer>
      {loading ? (
        <Box sx={{ py: 6, textAlign: 'center' }}>
          <CircularProgress size={28} />
        </Box>
      ) : appointments.length === 0 ? (
        <EmptyState message={`No ${TABS[tab].toLowerCase()} appointments`} />
      ) : (
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>ID</TableCell>
              <TableCell>Patient</TableCell>
              <TableCell>Doctor</TableCell>
              <TableCell>Time</TableCell>
              <TableCell>Notes</TableCell>
              <TableCell>Status</TableCell>
              {canManage && <TableCell align="right">Actions</TableCell>}
            </TableRow>
          </TableHead>

          <TableBody>
            {appointments.map(a => (
              <TableRow key={a.id} hover>
                <TableCell>#{a.id}</TableCell>
                <TableCell>{a.patientName} #{a.patientId}</TableCell>
                <TableCell>{a.doctorName} #{a.doctorId}</TableCell>
                <TableCell>{fmt(a.appointmentTime)}</TableCell>
                <TableCell sx={{ color: 'text.secondary', maxWidth: 160 }}>
                  {a.notes || '—'}
                </TableCell>
                <TableCell>
                  <StatusBadge status={a.status} />
                </TableCell>

                {canManage && (
                  <TableCell align="right">
                    {a.status === 'SCHEDULED' && (
                      <Box sx={{ display: 'flex', gap: 0.5, justifyContent: 'flex-end' }}>
                        <Tooltip title="Complete">
                          <IconButton
                            size="small"
                            color="success"
                            onClick={() =>
                              setConfirmAction({ type: 'complete', id: a.id })
                            }
                          >
                            <CheckCircleOutlineIcon fontSize="small" />
                          </IconButton>
                        </Tooltip>

                        <Tooltip title="Reschedule">
                          <IconButton
                            size="small"
                            color="primary"
                            onClick={() => {
                              setSelectedAppt(a);
                              setNewTime('');
                              setRescheduleOpen(true);
                            }}
                          >
                            <ScheduleIcon fontSize="small" />
                          </IconButton>
                        </Tooltip>

                        <Tooltip title="Cancel">
                          <IconButton
                            size="small"
                            color="error"
                            onClick={() =>
                              setConfirmAction({ type: 'cancel', id: a.id })
                            }
                          >
                            <CancelOutlinedIcon fontSize="small" />
                          </IconButton>
                        </Tooltip>
                      </Box>
                    )}
                  </TableCell>
                )}
              </TableRow>
            ))}
          </TableBody>
        </Table>
      )}
    </TableContainer>
  </Card>

  {/* Book Dialog */}
  <Dialog open={bookOpen} onClose={() => setBookOpen(false)} maxWidth="sm" fullWidth>
    <DialogTitle>Book appointment</DialogTitle>
    <DialogContent sx={{ pt: 2 }}>
      <ErrorMessage message={error} />

      <Box
        component="form"
        id="book-form"
        onSubmit={handleBook}
        sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 1 }}
      >
        <TextField
          label="Patient ID"
          value={form.patientId}
          onChange={e => setForm({ ...form, patientId: e.target.value })}
          required
          fullWidth
          type="number"
        />

        <FormControl fullWidth size="small">
          <InputLabel>Doctor</InputLabel>
          <Select
            label="Doctor"
            value={form.doctorId}
            onChange={e => setForm({ ...form, doctorId: e.target.value })}
            required
          >
            {doctors.map(d => (
              <MenuItem key={d.id} value={d.id}>
                Doctor #{d.id} — {d.department}
              </MenuItem>
            ))}
          </Select>
        </FormControl>

        <TextField
          label="Appointment time"
          type="datetime-local"
          value={form.appointmentTime}
          onChange={e => setForm({ ...form, appointmentTime: e.target.value })}
          required
          fullWidth
          InputLabelProps={{ shrink: true }}
        />

        <TextField
          label="Notes (optional)"
          value={form.notes}
          onChange={e => setForm({ ...form, notes: e.target.value })}
          fullWidth
          multiline
          rows={2}
        />
      </Box>
    </DialogContent>

    <DialogActions sx={{ px: 3, pb: 2 }}>
      <Button onClick={() => setBookOpen(false)} variant="outlined" size="small">
        Cancel
      </Button>
      <Button type="submit" form="book-form" variant="contained" size="small" disabled={submitting}>
        {submitting ? <CircularProgress size={18} sx={{ color: '#fff' }} /> : 'Book'}
      </Button>
    </DialogActions>
  </Dialog>

  {/* Reschedule Dialog */}
  <Dialog open={rescheduleOpen} onClose={() => setRescheduleOpen(false)} maxWidth="xs" fullWidth>
    <DialogTitle>Reschedule #{selectedAppt?.id}</DialogTitle>
    <DialogContent sx={{ pt: 2 }}>
      <ErrorMessage message={error} />

      <Box component="form" id="reschedule-form" onSubmit={handleReschedule} sx={{ mt: 1 }}>
        <TextField
          label="New time"
          type="datetime-local"
          value={newTime}
          onChange={e => setNewTime(e.target.value)}
          required
          fullWidth
          InputLabelProps={{ shrink: true }}
        />
      </Box>
    </DialogContent>

    <DialogActions sx={{ px: 3, pb: 2 }}>
      <Button onClick={() => setRescheduleOpen(false)} variant="outlined" size="small">
        Cancel
      </Button>
      <Button type="submit" form="reschedule-form" variant="contained" size="small" disabled={submitting}>
        {submitting ? <CircularProgress size={18} sx={{ color: '#fff' }} /> : 'Reschedule'}
      </Button>
    </DialogActions>
  </Dialog>

  {/* Confirm Dialog */}
  <ConfirmDialog
    open={!!confirmAction}
    title={confirmAction?.type === 'complete' ? 'Mark as completed?' : 'Cancel appointment?'}
    message={
      confirmAction?.type === 'complete'
        ? 'This will mark the appointment as completed.'
        : 'This will cancel the appointment. This cannot be undone.'
    }
    confirmLabel={confirmAction?.type === 'complete' ? 'Complete' : 'Cancel appointment'}
    confirmColor={confirmAction?.type === 'complete' ? 'success' : 'error'}
    onConfirm={handleAction}
    onCancel={() => setConfirmAction(null)}
  />
</Box>
);
}
