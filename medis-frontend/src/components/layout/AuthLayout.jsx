import { Outlet } from 'react-router-dom';
import { Box, Typography } from '@mui/material';

export default function AuthLayout() {
  return (
    <Box sx={{ minHeight: '100vh', display: 'flex', bgcolor: 'background.default' }}>
      <Box
        sx={{
          display: { xs: 'none', md: 'flex' },
          width: '45%',
          bgcolor: 'primary.main',
          flexDirection: 'column',
          justifyContent: 'center',
          p: 6,
        }}
      >
        <Box sx={{ width: 48, height: 48, borderRadius: 3, bgcolor: 'rgba(255,255,255,0.2)', display: 'flex', alignItems: 'center', justifyContent: 'center', mb: 3 }}>
          <Typography sx={{ color: '#fff', fontWeight: 700, fontSize: 22 }}>M</Typography>
        </Box>
        <Typography variant="h1" sx={{ color: '#fff', mb: 2, fontSize: '2.5rem' }}>Medis HMS</Typography>
        <Typography sx={{ color: 'rgba(255,255,255,0.75)', fontSize: '1rem', lineHeight: 1.8, maxWidth: 380 }}>
          A comprehensive hospital management system for doctors, patients, pharmacists, and administrative staff.
        </Typography>
        <Box sx={{ mt: 6, display: 'flex', gap: 3 }}>
          {['Appointments', 'Prescriptions', 'Inventory', 'Reports'].map((f) => (
            <Box key={f} sx={{ bgcolor: 'rgba(255,255,255,0.12)', borderRadius: 2, px: 2, py: 1 }}>
              <Typography sx={{ color: '#fff', fontSize: '0.8rem', fontWeight: 500 }}>{f}</Typography>
            </Box>
          ))}
        </Box>
      </Box>

      <Box sx={{ flex: 1, display: 'flex', alignItems: 'center', justifyContent: 'center', p: 3 }}>
        <Box sx={{ width: '100%', maxWidth: 420 }}>
          <Outlet />
        </Box>
      </Box>
    </Box>
  );
}
