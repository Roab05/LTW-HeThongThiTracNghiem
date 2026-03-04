import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function UserLayout() {
    const auth = useAuth();
    const navigate = useNavigate();

    return (
        <div className="appShell">
            <aside className="sidebar">
                <div className="sidebarTitle">Trang chủ</div>
                <div className="sidebarSub">Học tập &amp; thi cử</div>

                <nav className="navList">
                    <NavLink
                        to="/"
                        end
                        className={({ isActive }) => `navItem ${isActive ? 'navItemActive' : ''}`}
                    >
                        Danh sách bài thi <span>&gt;</span>
                    </NavLink>
                    <button
                        type="button"
                        className="navItem"
                        onClick={() => {
                            auth.logout();
                            navigate('/login');
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
