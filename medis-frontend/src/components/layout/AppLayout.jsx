import { useState } from 'react';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import {
  Box, Drawer, List, ListItemButton, ListItemIcon, ListItemText,
  Typography, Avatar, Divider, IconButton, AppBar, Toolbar, Tooltip, Menu, MenuItem,
  Dialog, DialogTitle, DialogContent, DialogActions, Button, TextField, CircularProgress,
} from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import LogoutIcon from '@mui/icons-material/Logout';
import LockOutlinedIcon from '@mui/icons-material/LockOutlined';
import PhoneOutlinedIcon from '@mui/icons-material/PhoneOutlined';
import { useAuth } from '../../hooks/useAuth';
import { navItems } from './navConfig';
import { RoleBadge, ErrorMessage } from '../common';
import { usersApi } from '../../api/services';

const DRAWER_WIDTH = 240;

export default function AppLayout() {
  const { user, signOut, role } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [mobileOpen, setMobileOpen] = useState(false);
  const [anchorEl, setAnchorEl] = useState(null);

  // Change phone
  const [phoneOpen, setPhoneOpen] = useState(false);
  const [phone, setPhone] = useState('');
  const [phoneError, setPhoneError] = useState('');
  const [phoneSubmitting, setPhoneSubmitting] = useState(false);

  const initials = user?.email?.slice(0, 2).toUpperCase() || 'US';
  const filtered = navItems.filter((n) => n.roles.includes(role));

  const handleLogout = () => { signOut(); navigate('/login'); };

  const handleChangePhone = async (e) => {
    e.preventDefault();
    setPhoneSubmitting(true); setPhoneError('');
    try {
      await usersApi.updatePhone(user.id, phone);
      setPhoneOpen(false);
      setPhone('');
    } catch (err) {
      setPhoneError(err.response?.data?.message || 'Failed to update phone number');
    }
    setPhoneSubmitting(false);
  };

  const drawer = (
    <Box sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      <Box sx={{ p: 2.5, display: 'flex', alignItems: 'center', gap: 1.5 }}>
        <Box sx={{ width: 36, height: 36, borderRadius: 2, bgcolor: 'primary.main', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
          <Typography sx={{ color: '#fff', fontWeight: 700, fontSize: 16, letterSpacing: -0.5 }}>M</Typography>
        </Box>
        <Box>
          <Typography variant="h4" sx={{ color: 'primary.main', lineHeight: 1 }}>Medis HMS</Typography>
          <Typography variant="caption" color="text.secondary">Hospital Management</Typography>
        </Box>
      </Box>

      <Divider sx={{ mx: 2, mb: 1 }} />

      <List sx={{ px: 1.5, flex: 1 }}>
        {filtered.map(({ label, path, icon: Icon }) => (
          <ListItemButton
            key={path}
            selected={location.pathname === path}
            onClick={() => { navigate(path); setMobileOpen(false); }}
            sx={{ mb: 0.5 }}
          >
            <ListItemIcon sx={{ minWidth: 36 }}>
              <Icon fontSize="small" />
            </ListItemIcon>
            <ListItemText primary={label} primaryTypographyProps={{ fontSize: '0.875rem', fontWeight: 500 }} />
          </ListItemButton>
        ))}
      </List>

      <Divider sx={{ mx: 2, mt: 1 }} />

      {/* User area — click to open menu */}
      <Box
        onClick={(e) => setAnchorEl(e.currentTarget)}
        sx={{ p: 2, display: 'flex', alignItems: 'center', gap: 1.5, cursor: 'pointer', borderRadius: 1, mx: 1, mb: 1, '&:hover': { bgcolor: 'action.hover' } }}
      >
        <Avatar sx={{ width: 36, height: 36, bgcolor: 'primary.light', fontSize: 13, fontWeight: 600 }}>
          {initials}
        </Avatar>
        <Box sx={{ flex: 1, minWidth: 0 }}>
          <Typography variant="body2" fontWeight={500} noWrap>{user?.email}</Typography>
          <RoleBadge role={role} size="small" />
        </Box>
        <Tooltip title="Sign out">
          <IconButton size="small" onClick={(e) => { e.stopPropagation(); handleLogout(); }} color="default">
            <LogoutIcon fontSize="small" />
          </IconButton>
        </Tooltip>
      </Box>

      <Menu
        anchorEl={anchorEl}
        open={Boolean(anchorEl)}
        onClose={() => setAnchorEl(null)}
        anchorOrigin={{ vertical: 'top', horizontal: 'right' }}
        transformOrigin={{ vertical: 'bottom', horizontal: 'right' }}
      >
        <MenuItem onClick={() => { setAnchorEl(null); navigate('/change-password'); }}>
          <LockOutlinedIcon fontSize="small" sx={{ mr: 1.5, color: 'text.secondary' }} />
          Change password
        </MenuItem>
        <MenuItem onClick={() => { setAnchorEl(null); setPhoneError(''); setPhone(''); setPhoneOpen(true); }}>
          <PhoneOutlinedIcon fontSize="small" sx={{ mr: 1.5, color: 'text.secondary' }} />
          Change phone number
        </MenuItem>
      </Menu>
    </Box>
  );

  return (
    <Box sx={{ display: 'flex', minHeight: '100vh', bgcolor: 'background.default' }}>
      <AppBar
        position="fixed"
        elevation={0}
        sx={{
          display: { md: 'none' },
          bgcolor: 'background.paper',
          borderBottom: '0.5px solid rgba(0,0,0,0.08)',
          width: '100%',
        }}
      >
        <Toolbar>
          <IconButton edge="start" onClick={() => setMobileOpen(true)} sx={{ mr: 2, color: 'text.primary' }}>
            <MenuIcon />
          </IconButton>
          <Typography variant="h4" color="primary">Medis HMS</Typography>
        </Toolbar>
      </AppBar>

      <Drawer
        variant="temporary"
        open={mobileOpen}
        onClose={() => setMobileOpen(false)}
        ModalProps={{ keepMounted: true }}
        sx={{ display: { xs: 'block', md: 'none' }, '& .MuiDrawer-paper': { width: DRAWER_WIDTH } }}
      >
        {drawer}
      </Drawer>

      <Drawer
        variant="permanent"
        sx={{ display: { xs: 'none', md: 'block' }, '& .MuiDrawer-paper': { width: DRAWER_WIDTH, position: 'relative' } }}
      >
        {drawer}
      </Drawer>

      <Box component="main" sx={{ flex: 1, p: 3, mt: { xs: 7, md: 0 }, minWidth: 0 }}>
        <Outlet />
      </Box>

      {/* Change Phone Dialog */}
      <Dialog open={phoneOpen} onClose={() => setPhoneOpen(false)} maxWidth="xs" fullWidth>
        <DialogTitle>Change phone number</DialogTitle>
        <DialogContent sx={{ pt: 2 }}>
          <ErrorMessage message={phoneError} />
          <Box component="form" id="phone-form" onSubmit={handleChangePhone}
            sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 1 }}>
            <TextField
              label="New phone number"
              type="tel"
              required
              fullWidth
              value={phone}
              onChange={e => setPhone(e.target.value)}
            />
          </Box>
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2 }}>
          <Button onClick={() => setPhoneOpen(false)} variant="outlined" size="small">Cancel</Button>
          <Button type="submit" form="phone-form" variant="contained" size="small" disabled={phoneSubmitting}>
            {phoneSubmitting ? <CircularProgress size={18} sx={{ color: '#fff' }} /> : 'Update'}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}