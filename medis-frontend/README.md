# Medis HMS — Frontend

React + Material UI frontend for the Medis Hospital Management System.

## Stack
- **React 19** + **Vite 8**
- **MUI (Material UI) v6** — Material Design component library
- **Redux Toolkit** — state management (auth slice)
- **React Router v7** — client-side routing with role guards
- **Axios** — HTTP client with JWT interceptors + auto-refresh

## Getting Started

### Prerequisites
- Node.js 18+
- Medis HMS backend running on `http://localhost:8080`

### Install & run

```bash
npm install
npm run dev
```

App starts at **http://localhost:5173**

### Configure API URL

Edit `.env` to point at your backend:

```
VITE_API_URL=http://localhost:8080/api/v1
```

## Project Structure

```
src/
├── api/
│   ├── client.js        # Axios instance with JWT + refresh interceptors
│   └── services.js      # All API calls organized by domain
├── app/
│   ├── store.js         # Redux store
│   └── slices/
│       └── authSlice.js # Auth state + login thunk
├── components/
│   ├── common/          # Shared: RoleBadge, StatusBadge, StatCard, PageHeader, ConfirmDialog
│   └── layout/
│       ├── AppLayout.jsx    # Sidebar + mobile drawer + user menu
│       ├── AuthLayout.jsx   # Split-panel login layout
│       └── navConfig.js     # Nav items filtered by role
├── hooks/
│   └── useAuth.js       # Role helpers: isAdmin, isDoctor, hasRole(), etc.
├── pages/
│   ├── LoginPage.jsx
│   ├── ChangePasswordPage.jsx
│   ├── DashboardPage.jsx
│   ├── AppointmentsPage.jsx
│   ├── DoctorsPage.jsx
│   ├── PatientsPage.jsx
│   ├── MedicinesPage.jsx
│   ├── InventoryPage.jsx
│   ├── UsersPage.jsx
│   ├── ProfilePage.jsx
│   └── UnauthorizedPage.jsx
├── routes/
│   ├── index.jsx        # Browser router + all route definitions
│   └── guards.jsx       # RequireAuth, RequireGuest
└── theme/
    └── index.js         # MUI theme — Teal palette, typography, component overrides
```

## Role-based access

| Page | ADMIN | DOCTOR | PATIENT | PHARMACIST | RECEPTIONIST |
|---|:---:|:---:|:---:|:---:|:---:|
| Dashboard | ✓ | ✓ | ✓ | ✓ | ✓ |
| Appointments | ✓ | ✓ | ✓ | | ✓ |
| Doctors | ✓ | ✓ | | | |
| Patients | ✓ | ✓ | | | ✓ |
| Medicines | ✓ | ✓ | | ✓ | |
| Inventory | ✓ | | | ✓ | |
| Users | ✓ | | | | |

## Auth flow

1. `POST /auth/login` → stores `accessToken` + `refreshToken` in localStorage
2. If `passwordChanged: false` → redirected to `/change-password` before accessing anything
3. On 401 response → automatically attempts token refresh, retries original request
4. On logout → clears localStorage, redirects to `/login`

## Build for production

```bash
npm run build
```

Output goes to `dist/`. Serve with any static file server or configure your backend to serve it.
