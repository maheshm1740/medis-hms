import { createBrowserRouter, RouterProvider, Navigate } from 'react-router-dom';
import { RequireAuth, RequireGuest } from './guards';
import AppLayout from '../components/layout/AppLayout';
import AuthLayout from '../components/layout/AuthLayout';

import LoginPage from '../pages/LoginPage';
import ChangePasswordPage from '../pages/ChangePasswordPage';
import UnauthorizedPage from '../pages/UnauthorizedPage';
import DashboardPage from '../pages/DashboardPage';
import DoctorsPage from '../pages/DoctorsPage';
import PatientsPage from '../pages/PatientsPage';
import AppointmentsPage from '../pages/AppointmentsPage';
import MedicinesPage from '../pages/MedicinesPage';
import InventoryPage from '../pages/InventoryPage';
import UsersPage from '../pages/UsersPage';
import ProfilePage from '../pages/ProfilePage';
import RegisterPage from '../pages/RegisterPage';

const router = createBrowserRouter([
  {
    path: '/',
    element: <RequireAuth><AppLayout /></RequireAuth>,
    children: [
      { index: true, element: <DashboardPage /> },
      { path: 'profile', element: <ProfilePage /> },
      { path: 'appointments', element: <AppointmentsPage /> },
      {
        path: 'doctors',
        element: <RequireAuth roles={['ADMIN', 'DOCTOR']}><DoctorsPage /></RequireAuth>,
      },
      {
        path: 'patients',
        element: <RequireAuth roles={['ADMIN', 'DOCTOR', 'RECEPTIONIST']}><PatientsPage /></RequireAuth>,
      },
      {
        path: 'medicines',
        element: <RequireAuth roles={['ADMIN', 'PHARMACIST', 'DOCTOR']}><MedicinesPage /></RequireAuth>,
      },
      {
        path: 'inventory',
        element: <RequireAuth roles={['ADMIN', 'PHARMACIST']}><InventoryPage /></RequireAuth>,
      },
      {
        path: 'users',
        element: <RequireAuth roles={['ADMIN']}><UsersPage /></RequireAuth>,
      },
    ],
  },
  {
    element: <AuthLayout />,
    children: [
      { path: 'login', element: <RequireGuest><LoginPage /></RequireGuest> },
      { path: 'register', element: <RequireGuest><RegisterPage /></RequireGuest> },
      { path: 'change-password', element: <RequireAuth><ChangePasswordPage /></RequireAuth> },
      { path: 'unauthorized', element: <UnauthorizedPage /> },
    ],
  },
  { path: '*', element: <Navigate to="/" replace /> },
]);

export default function AppRouter() {
  return <RouterProvider router={router} />;
}
