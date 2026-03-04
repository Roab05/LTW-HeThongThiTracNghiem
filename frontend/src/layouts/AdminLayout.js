import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function AdminLayout() {
    const auth = useAuth();
    const navigate = useNavigate();

    return (
        <div className="appShell">
            <aside className="sidebar">
                <div className="sidebarTitle">Admin</div>
                <div className="sidebarSub">Quản trị hệ thống</div>

                <nav className="navList">
                    <NavLink
                        to="/admin"
                        end
                        className={({ isActive }) => `navItem ${isActive ? 'navItemActive' : ''}`}
                    >
                        Dashboard <span>&gt;</span>
                    </NavLink>
                    <NavLink
                        to="/admin/exams"
                        className={({ isActive }) => `navItem ${isActive ? 'navItemActive' : ''}`}
                    >
                        Kỳ thi <span>&gt;</span>
                    </NavLink>
                    <NavLink
                        to="/admin/users"
                        className={({ isActive }) => `navItem ${isActive ? 'navItemActive' : ''}`}
                    >
                        Người dùng <span>&gt;</span>
                    </NavLink>
                    <NavLink
                        to="/admin/statistics"
                        className={({ isActive }) => `navItem ${isActive ? 'navItemActive' : ''}`}
                    >
                        Thống kê <span>&gt;</span>
                    </NavLink>
                    <NavLink
                        to="/admin/students"
                        className={({ isActive }) => `navItem ${isActive ? 'navItemActive' : ''}`}
                    >
                        KQ từng SV <span>&gt;</span>
                    </NavLink>
                    <button
                        type="button"
                        className="navItem"
                        onClick={() => {
                            auth.logout();
                            navigate('/admin/login');
                        }}
                    >
                        Đăng xuất <span>&gt;</span>
                    </button>
                </nav>
            </aside>
            <main className="main">
                <Outlet />
            </main>
        </div>
    );
}
