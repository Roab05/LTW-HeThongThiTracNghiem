import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function RequireAdmin({ children }) {
    const auth = useAuth();
    const location = useLocation();

    if (!auth.session) {
        return <Navigate to="/admin/login" replace state={{ from: location.pathname }} />;
    }

    if (auth.session.role !== 'admin') {
        return <Navigate to="/" replace />;
    }

    return children;
}
