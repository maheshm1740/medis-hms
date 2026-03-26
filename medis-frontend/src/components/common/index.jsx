import { Chip, Box, Typography, Button, Dialog, DialogTitle, DialogContent, DialogActions, Card, CardContent } from '@mui/material';

const ROLE_COLORS = {
  ADMIN: { bg: '#E6F1FB', color: '#0C447C' },
  DOCTOR: { bg: '#E1F5EE', color: '#085041' },
  PATIENT: { bg: '#EEEDFE', color: '#3C3489' },
  PHARMACIST: { bg: '#FAEEDA', color: '#633806' },
  RECEPTIONIST: { bg: '#FAECE7', color: '#712B13' },
};

const STATUS_COLORS = {
  SCHEDULED: { bg: '#E1F5EE', color: '#0F6E56' },
  COMPLETED: { bg: '#EAF3DE', color: '#3B6D11' },
  CANCELLED: { bg: '#FCEBEB', color: '#A32D2D' },
};

export function RoleBadge({ role, size = 'medium' }) {
  const c = ROLE_COLORS[role] || { bg: '#F1EFE8', color: '#444441' };
  return (
    <Chip
      label={role}
      size={size === 'small' ? 'small' : 'small'}
      sx={{ bgcolor: c.bg, color: c.color, fontWeight: 600, fontSize: '0.68rem', height: 20 }}
    />
  );
}

export function StatusBadge({ status }) {
  const c = STATUS_COLORS[status] || { bg: '#F1EFE8', color: '#444441' };
  return (
    <Chip
      label={status}
      size="small"
      sx={{ bgcolor: c.bg, color: c.color, fontWeight: 600, fontSize: '0.68rem', height: 20 }}
    />
  );
}

export function PageHeader({ title, subtitle, action }) {
  return (
    <Box sx={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between', mb: 3 }}>
      <Box>
        <Typography variant="h2" gutterBottom={false}>{title}</Typography>
        {subtitle && <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5 }}>{subtitle}</Typography>}
      </Box>
      {action}
    </Box>
  );
}

export function StatCard({ label, value, color }) {
  return (
    <Card>
      <CardContent sx={{ bgcolor: 'background.default', '&:last-child': { pb: 2 } }}>
        <Typography variant="caption" color="text.secondary" sx={{ textTransform: 'uppercase', letterSpacing: '0.06em', fontWeight: 600 }}>
          {label}
        </Typography>
        <Typography variant="h1" sx={{ mt: 0.5, color: color || 'text.primary', fontSize: '2rem' }}>
          {value}
        </Typography>
      </CardContent>
    </Card>
  );
}

export function ConfirmDialog({ open, title, message, onConfirm, onCancel, confirmColor = 'error', confirmLabel = 'Confirm' }) {
  return (
    <Dialog open={open} onClose={onCancel} maxWidth="xs" fullWidth>
      <DialogTitle>{title}</DialogTitle>
      <DialogContent>
        <Typography variant="body2" color="text.secondary">{message}</Typography>
      </DialogContent>
      <DialogActions sx={{ px: 3, pb: 2 }}>
        <Button onClick={onCancel} variant="outlined" size="small">Cancel</Button>
        <Button onClick={onConfirm} variant="contained" color={confirmColor} size="small">{confirmLabel}</Button>
      </DialogActions>
    </Dialog>
  );
}

export function EmptyState({ message = 'No data found' }) {
  return (
    <Box sx={{ py: 8, textAlign: 'center' }}>
      <Typography color="text.secondary" variant="body2">{message}</Typography>
    </Box>
  );
}

export function ErrorMessage({ message }) {
  if (!message) return null;
  return (
    <Box sx={{ p: 1.5, bgcolor: '#FCEBEB', border: '0.5px solid #F09595', borderRadius: 2, mb: 2 }}>
      <Typography variant="body2" sx={{ color: '#A32D2D' }}>{message}</Typography>
    </Box>
  );
}
