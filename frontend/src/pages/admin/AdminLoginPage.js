import { useMemo, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { required } from '../../utils/validate';

export default function AdminLoginPage() {
    const auth = useAuth();
    const navigate = useNavigate();
    const [form, setForm] = useState({ username: '', password: '' });
    const [error, setError] = useState('');
    const canSubmit = useMemo(() => required(form.username) && required(form.password), [form]);

    async function onSubmit(e) {
        e.preventDefault();
        setError('');
        if (!canSubmit) {
            setError('Vui lòng nhập đầy đủ thông tin.');
            return;
        }
        const ok = await auth.loginAdmin(form.username.trim(), form.password);
        if (!ok) {
            setError('Sai tài khoản admin. (Demo: admin / admin123)');
            return;
        }
        navigate('/admin');
    }

    return (
        <div className="authShell">
            <div className="authPanel">
                <form className="authCard" onSubmit={onSubmit}>
                    <div className="logoMark">
                        <img src={`${process.env.PUBLIC_URL}/ptit-logo.png`} alt="PTIT" />
                    </div>
                    <div className="authTitle">Admin Login</div>

                    <div className="field">
                        <div className="label">Tài khoản</div>
                        <input
                            className="input"
                            value={form.username}
                            onChange={(e) => setForm((s) => ({ ...s, username: e.target.value }))}
                            placeholder="admin"
                            autoComplete="username"
                        />
                    </div>

                    <div className="field">
                        <div className="label">Mật khẩu</div>
                        <input
                            className="input"
                            value={form.password}
                            onChange={(e) => setForm((s) => ({ ...s, password: e.target.value }))}
                            placeholder="admin123"
                            type="password"
                            autoComplete="current-password"
                        />
                    </div>

                    {error ? <div className="error">{error}</div> : null}

                    <div style={{ marginTop: 16 }}>
                        <button className="btn btnSolid" type="submit" disabled={!canSubmit}>
                            ĐĂNG NHẬP
                        </button>
                    </div>

                    <div style={{ marginTop: 12, textAlign: 'center' }}>
                        <Link className="link" to="/login">
                            Về đăng nhập sinh viên
                        </Link>
                    </div>
                </form>
            </div>
            <div className="heroRight" />
        </div>
    );
}
