import { Link, useNavigate } from 'react-router-dom';
import { useMemo, useState } from 'react';
import { createUser } from '../../services/db';
import { isEmail, required } from '../../utils/validate';

export default function RegisterPage() {
    const navigate = useNavigate();
    const [form, setForm] = useState({
        fullName: '',
        username: '',
        email: '',
        password: '',
        confirm: '',
    });
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const canSubmit = useMemo(() => {
        if (!required(form.fullName)) return false;
        if (!required(form.username)) return false;
        if (!isEmail(form.email)) return false;
        if (String(form.password).length < 6) return false;
        if (form.password !== form.confirm) return false;
        return true;
    }, [form]);

    function validate() {
        if (!required(form.fullName)) return 'Vui lòng nhập Tên người dùng.';
        if (!required(form.username)) return 'Vui lòng nhập Tên đăng nhập.';
        if (!isEmail(form.email)) return 'Email không hợp lệ.';
        if (String(form.password).length < 6) return 'Mật khẩu tối thiểu 6 ký tự.';
        if (form.password !== form.confirm) return 'Xác nhận mật khẩu không khớp.';
        return '';
    }

    function onSubmit(e) {
        e.preventDefault();
        setError('');
        setSuccess('');
        const msg = validate();
        if (msg) {
            setError(msg);
            return;
        }

        const res = createUser({
            username: form.username.trim(),
            fullName: form.fullName.trim(),
            email: form.email.trim(),
            password: form.password,
        });
        if (!res.ok) {
            setError(res.error || 'Đăng ký thất bại.');
            return;
        }
        setSuccess('Đăng ký thành công. Bạn có thể đăng nhập ngay.');
        setTimeout(() => navigate('/login'), 600);
    }

    return (
        <div className="authShell">
            <div className="authPanel">
                <form className="authCard" onSubmit={onSubmit}>
                    <div className="logoMark">
                        <img src={`${process.env.PUBLIC_URL}/ptit-logo.png`} alt="PTIT" />
                    </div>
                    <div className="authTitle">Đăng ký</div>

                    <div className="field">
                        <div className="label">Tên người dùng</div>
                        <input
                            className="input"
                            value={form.fullName}
                            onChange={(e) => setForm((s) => ({ ...s, fullName: e.target.value }))}
                            placeholder="Nhập họ và tên"
                        />
                    </div>

                    <div className="field">
                        <div className="label">Tên đăng nhập (MSSV)</div>
                        <input
                            className="input"
                            value={form.username}
                            onChange={(e) => setForm((s) => ({ ...s, username: e.target.value }))}
                            placeholder="Ví dụ: 230056PCNCT"
                            autoComplete="username"
                        />
                    </div>

                    <div className="field">
                        <div className="label">Email</div>
                        <input
                            className="input"
                            value={form.email}
                            onChange={(e) => setForm((s) => ({ ...s, email: e.target.value }))}
                            placeholder="sv@demo.local"
                            type="email"
                            autoComplete="email"
                        />
                    </div>

                    <div className="field">
                        <div className="label">Mật khẩu</div>
                        <input
                            className="input"
                            value={form.password}
                            onChange={(e) => setForm((s) => ({ ...s, password: e.target.value }))}
                            placeholder="Tối thiểu 6 ký tự"
                            type="password"
                            autoComplete="new-password"
                        />
                    </div>

                    <div className="field">
                        <div className="label">Xác nhận mật khẩu</div>
                        <input
                            className="input"
                            value={form.confirm}
                            onChange={(e) => setForm((s) => ({ ...s, confirm: e.target.value }))}
                            placeholder="Nhập lại mật khẩu"
                            type="password"
                            autoComplete="new-password"
                        />
                    </div>

                    {error ? <div className="error">{error}</div> : null}
                    {success ? <div className="success">{success}</div> : null}

                    <div style={{ marginTop: 16 }}>
                        <button className="btn btnSolid" type="submit" disabled={!canSubmit}>
                            TẠO TÀI KHOẢN
                        </button>
                    </div>

                    <div style={{ marginTop: 12, textAlign: 'center' }}>
                        <Link className="link" to="/login">
                            Quay lại đăng nhập
                        </Link>
                    </div>
                </form>
            </div>
            <div className="heroRight" />
        </div>
    );
}
