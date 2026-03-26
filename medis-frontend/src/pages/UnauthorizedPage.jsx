import { Box, Typography, Button } from '@mui/material';
import { useNavigate } from 'react-router-dom';

export default function UnauthorizedPage() {
  const navigate = useNavigate();
  return (
    <Box sx={{ minHeight: '100vh', display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', gap: 2 }}>
      <Typography variant="h1" color="primary" sx={{ fontSize: '5rem', fontWeight: 700 }}>403</Typography>
      <Typography variant="h3">Access denied</Typography>
      <Typography color="text.secondary">You don't have permission to view this page.</Typography>
      <Button variant="contained" onClick={() => navigate('/')}>Go to dashboard</Button>
    </Box>
  );
}
