import { createTheme } from '@mui/material/styles';

const theme = createTheme({
  palette: {
    primary: {
      light: '#1D9E75',
      main: '#0F6E56',
      dark: '#085041',
      contrastText: '#fff',
    },
    secondary: {
      main: '#185FA5',
      contrastText: '#fff',
    },
    error: { main: '#A32D2D' },
    warning: { main: '#854F0B' },
    success: { main: '#3B6D11' },
    info: { main: '#185FA5' },
    background: {
      default: '#F5F7F6',
      paper: '#FFFFFF',
    },
    text: {
      primary: '#1A1A1A',
      secondary: '#5F5E5A',
    },
  },
  typography: {
    fontFamily: '"Roboto", "Helvetica", "Arial", sans-serif',
    h1: { fontSize: '2rem', fontWeight: 500 },
    h2: { fontSize: '1.5rem', fontWeight: 500 },
    h3: { fontSize: '1.25rem', fontWeight: 500 },
    h4: { fontSize: '1rem', fontWeight: 500 },
    h5: { fontSize: '0.875rem', fontWeight: 500 },
    h6: { fontSize: '0.75rem', fontWeight: 500 },
    body1: { fontSize: '0.875rem' },
    body2: { fontSize: '0.8125rem' },
    caption: { fontSize: '0.75rem' },
  },
  shape: { borderRadius: 10 },
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          textTransform: 'none',
          fontWeight: 500,
          borderRadius: 8,
          boxShadow: 'none',
          '&:hover': { boxShadow: 'none' },
        },
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          boxShadow: 'none',
          border: '0.5px solid rgba(0,0,0,0.10)',
          borderRadius: 12,
        },
      },
    },
    MuiPaper: {
      styleOverrides: {
        root: { boxShadow: 'none' },
      },
    },
    MuiTableHead: {
      styleOverrides: {
        root: {
          '& .MuiTableCell-head': {
            fontSize: '0.7rem',
            fontWeight: 600,
            textTransform: 'uppercase',
            letterSpacing: '0.06em',
            color: '#5F5E5A',
            backgroundColor: '#F5F7F6',
          },
        },
      },
    },
    MuiTableCell: {
      styleOverrides: {
        root: {
          borderColor: 'rgba(0,0,0,0.07)',
          fontSize: '0.875rem',
          padding: '10px 16px',
        },
      },
    },
    MuiChip: {
      styleOverrides: {
        root: {
          fontWeight: 500,
          fontSize: '0.72rem',
          height: 24,
          borderRadius: 20,
        },
      },
    },
    MuiTextField: {
      defaultProps: { size: 'small', variant: 'outlined' },
    },
    MuiSelect: {
      defaultProps: { size: 'small' },
    },
    MuiDrawer: {
      styleOverrides: {
        paper: {
          border: 'none',
          borderRight: '0.5px solid rgba(0,0,0,0.08)',
        },
      },
    },
    MuiListItemButton: {
      styleOverrides: {
        root: {
          borderRadius: 8,
          marginBottom: 2,
          '&.Mui-selected': {
            backgroundColor: '#E1F5EE',
            color: '#0F6E56',
            '& .MuiListItemIcon-root': { color: '#0F6E56' },
            '&:hover': { backgroundColor: '#d0ede5' },
          },
        },
      },
    },
    MuiAlert: {
      styleOverrides: {
        root: { borderRadius: 8, fontSize: '0.875rem' },
      },
    },
  },
});

export default theme;
