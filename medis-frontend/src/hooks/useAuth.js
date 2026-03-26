import { useSelector, useDispatch } from 'react-redux';
import { logout } from '../app/slices/authSlice';

export const useAuth = () => {
  const dispatch = useDispatch();
  const { user, token, loading, error } = useSelector((s) => s.auth);

  const isAuthenticated = !!token;
  const role = user?.role;

  const isAdmin = role === 'ADMIN';
  const isDoctor = role === 'DOCTOR';
  const isPatient = role === 'PATIENT';
  const isPharmacist = role === 'PHARMACIST';
  const isReceptionist = role === 'RECEPTIONIST';

  const hasRole = (...roles) => roles.includes(role);

  const signOut = () => dispatch(logout());

  return { user, token, loading, error, isAuthenticated, role, isAdmin, isDoctor, isPatient, isPharmacist, isReceptionist, hasRole, signOut };
};
