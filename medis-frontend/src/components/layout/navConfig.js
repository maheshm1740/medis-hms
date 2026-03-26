import DashboardIcon from '@mui/icons-material/Dashboard';
import EventIcon from '@mui/icons-material/Event';
import LocalHospitalIcon from '@mui/icons-material/LocalHospital';
import PeopleIcon from '@mui/icons-material/People';
import MedicationIcon from '@mui/icons-material/Medication';
import InventoryIcon from '@mui/icons-material/Inventory';
import ManageAccountsIcon from '@mui/icons-material/ManageAccounts';

export const navItems = [
  { label: 'Dashboard', path: '/', icon: DashboardIcon, roles: ['ADMIN', 'DOCTOR', 'PATIENT', 'PHARMACIST', 'RECEPTIONIST'] },
  { label: 'Appointments', path: '/appointments', icon: EventIcon, roles: ['ADMIN', 'DOCTOR', 'PATIENT', 'RECEPTIONIST'] },
  { label: 'Doctors', path: '/doctors', icon: LocalHospitalIcon, roles: ['ADMIN', 'DOCTOR'] },
  { label: 'Patients', path: '/patients', icon: PeopleIcon, roles: ['ADMIN', 'DOCTOR', 'RECEPTIONIST'] },
  { label: 'Medicines', path: '/medicines', icon: MedicationIcon, roles: ['ADMIN', 'PHARMACIST', 'DOCTOR'] },
  { label: 'Inventory', path: '/inventory', icon: InventoryIcon, roles: ['ADMIN', 'PHARMACIST'] },
  { label: 'Users', path: '/users', icon: ManageAccountsIcon, roles: ['ADMIN'] },
];
