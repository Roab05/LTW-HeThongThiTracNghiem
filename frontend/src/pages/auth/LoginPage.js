import { Link, useNavigate } from 'react-router-dom';
import { useMemo, useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { required } from '../../utils/validate';

export default function LoginPage() {
    const auth = useAuth();
    const navigate = useNavigate();

    const [form, setForm] = useState({ username: '', password: '' });
    const [error, setError] = useState('');
    const canSubmit = useMemo(() => required(form.username) && required(form.password), [form]);

    async function onSubmit(e) {
        e.preventDefault();
        setError('');
        if (!canSubmit) {
            setError('Vui lòng nhập đầy đủ Tên đăng nhập và Mật khẩu.');
            return;
        }
        const user = await auth.loginUser(form.username.trim(), form.password);
        if (!user) {
            setError('Sai tên đăng nhập hoặc mật khẩu. (Demo: 230056PCNCT / 123456)');
            return;
        }
        navigate('/');
    }

    return (
        <div className="authShell">
            <div className="authPanel">
                <form className="authCard" onSubmit={onSubmit}>
                    <div className="logoMark">
                        <img src={`${process.env.PUBLIC_URL}/ptit-logo.png`} alt="PTIT" />
                    </div>

                    <div className="field">
                        <div className="label">Tên đăng nhập</div>
                        <input
                            className="input"
                            value={form.username}
                            onChange={(e) => setForm((s) => ({ ...s, username: e.target.value }))}
                            placeholder="Nhập tên đăng nhập"
                            autoComplete="username"
                        />
                    </div>

                    <div className="field">
                        <div className="label">Mật khẩu</div>
                        <input
                            className="input"
                            value={form.password}
                            onChange={(e) => setForm((s) => ({ ...s, password: e.target.value }))}
                            placeholder="Nhập mật khẩu"
                            type="password"
                            autoComplete="current-password"
                        />
                    </div>

                    {error ? <div className="error">{error}</div> : null}

                    <div className="helpRow" style={{ marginTop: 10 }}>
                        <span className="muted">Quên mật khẩu</span>
                        <Link className="link" to="/admin/login">
                            Admin
                        </Link>
                    </div>

                    <div style={{ marginTop: 16 }}>
                        <button className="btn btnSolid btnAuthLogin" type="submit" disabled={!canSubmit}>
                            Đăng Nhập
                        </button>
                    </div>

                    <div style={{ marginTop: 14, textAlign: 'center', color: '#fff' }} />
                </form>

                <div style={{ marginTop: 18, color: '#fff', fontWeight: 800 }}>
                    Chưa có tài khoản?{' '}
                    <Link className="link" to="/register" style={{ color: '#fff', textDecoration: 'underline' }}>
                        Đăng ký
                    </Link>
                </div>
            </div>
            <div
                className="heroRight"
                style={{ backgroundImage: `url(${process.env.PUBLIC_URL}/ptit-gate.png)` }}
            />
        </div>
    );
}
