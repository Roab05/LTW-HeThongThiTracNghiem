import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function RequireUser({ children }) {
    const auth = useAuth();
    const location = useLocation();

    if (!auth.session) {
        return <Navigate to="/login" replace state={{ from: location.pathname }} />;
    }

    if (auth.session.role !== 'user') {
        return <Navigate to="/admin" replace />;
    }

    return children;
}
