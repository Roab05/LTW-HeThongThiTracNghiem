import { createContext, useContext, useMemo, useState } from 'react';
import {
    clearSession,
    getSession,
    loginAdminWithPassword,
    loginUserWithPassword,
    saveSession,
} from '../services/db';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
    const [session, setSession] = useState(() => getSession());

    const value = useMemo(() => {
        const isUser = session?.role === 'user';
        const isAdmin = session?.role === 'admin';

        return {
            session,
            isUser,
            isAdmin,
            async loginUser(username, password) {
                const user = loginUserWithPassword(username, password);
                const next = user ? { role: 'user', userId: user.id } : null;
                if (next) {
                    saveSession(next);
                    setSession(next);
                }
                return user;
            },
            async loginAdmin(username, password) {
                const ok = loginAdminWithPassword(username, password);
                const next = ok ? { role: 'admin', adminId: 'admin' } : null;
                if (next) {
                    saveSession(next);
                    setSession(next);
                }
                return ok;
            },
            logout() {
                clearSession();
                setSession(null);
            },
        };
    }, [session]);

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
    const ctx = useContext(AuthContext);
    if (!ctx) throw new Error('useAuth must be used within AuthProvider');
    return ctx;
}
