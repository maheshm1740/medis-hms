import { useEffect, useState } from 'react';
import { Box, Grid, Card, CardContent, Typography, Divider, Chip } from '@mui/material';
import { useAuth } from '../hooks/useAuth';
import { appointmentsApi, doctorsApi, patientsApi, inventoryApi, medicinesApi } from '../api/services';
import { PageHeader, StatCard, StatusBadge } from '../components/common';

export default function DashboardPage() {
  const { user, role, isAdmin, isDoctor, isPatient, isPharmacist, isReceptionist } = useAuth();
  const [appointments, setAppointments] = useState([]);
  const [stats, setStats] = useState({});
  const [loading, setLoading] = useState(true);

  const greeting = () => {
    const h = new Date().getHours();
    if (h < 12) return 'Good morning';
    if (h < 17) return 'Good afternoon';
    return 'Good evening';
  };

  useEffect(() => {
    const load = async () => {
      try {
        const [scheduled] = await Promise.all([
          appointmentsApi.getByStatus('SCHEDULED'),
        ]);
        setAppointments(scheduled.data.slice(0, 5));

        if (isAdmin || isReceptionist) {
          const [docs, pats] = await Promise.all([doctorsApi.getAll(), patientsApi.getById]);
          setStats(s => ({ ...s, scheduledCount: scheduled.data.length }));
        }
        setStats(s => ({ ...s, scheduledCount: scheduled.data.length }));
      } catch {}
      setLoading(false);
    };
    load();
  }, []);

  const date = new Date().toLocaleDateString('en-IN', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' });

  return (
    <Box>
      <PageHeader
        title={`${greeting()}`}
        subtitle={date}
      />

      <Grid container spacing={2} sx={{ mb: 3 }}>
        <Grid item xs={12} sm={4}>
          <StatCard label="Scheduled appointments" value={stats.scheduledCount ?? '—'} color="primary.main" />
        </Grid>
        {(isAdmin || isReceptionist) && (
          <Grid item xs={12} sm={4}>
            <StatCard label="Active role" value={role} />
          </Grid>
        )}
        <Grid item xs={12} sm={4}>
          <StatCard label="System status" value="Online" color="success.main" />
        </Grid>
      </Grid>

      <Card>
        <CardContent>
          <Typography variant="h4" gutterBottom>Upcoming appointments</Typography>
          <Divider sx={{ mb: 2 }} />
          {appointments.length === 0 ? (
            <Typography variant="body2" color="text.secondary" sx={{ py: 3, textAlign: 'center' }}>
              No upcoming appointments
            </Typography>
          ) : (
            appointments.map((appt) => (
              <Box key={appt.id} sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', py: 1.5, borderBottom: '0.5px solid rgba(0,0,0,0.06)' }}>
                <Box>
                  <Typography variant="body2" fontWeight={500}>Appointment #{appt.id}</Typography>
                  <Typography variant="caption" color="text.secondary">
                    {new Date(appt.appointmentTime).toLocaleString('en-IN', { dateStyle: 'medium', timeStyle: 'short' })}
                  </Typography>
                </Box>
                <StatusBadge status={appt.status} />
              </Box>
            ))
          )}
        </CardContent>
      </Card>
    </Box>
  );
}
