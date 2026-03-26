import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

export const RequireAuth = ({ children, roles }) => {
  const { isAuthenticated, role } = useAuth();
  const location = useLocation();

  if (!isAuthenticated) return <Navigate to="/login" state={{ from: location }} replace />;
  if (roles && !roles.includes(role)) return <Navigate to="/unauthorized" replace />;
  return children;
};

export const RequireGuest = ({ children }) => {
  const { isAuthenticated } = useAuth();
  if (isAuthenticated) return <Navigate to="/" replace />;
  return children;
};
