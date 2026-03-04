import { useMemo, useState } from 'react';
import { deleteUser, listUsers, upsertUser } from '../../services/db';

function buildNewUser() {
    return {
        id: '',
        role: 'user',
        username: '',
        fullName: '',
        email: '',
        password: '123456',
        studentCode: '',
    };
}

export default function AdminUsersPage() {
    const [tick, setTick] = useState(0);
    const [editing, setEditing] = useState(null);
    const users = useMemo(() => {
        void tick;
        return listUsers().filter((u) => u.role === 'user');
    }, [tick]);

    const current = editing || buildNewUser();

    function save() {
        if (!String(current.username).trim()) return;
        if (!String(current.fullName).trim()) return;
        if (!String(current.email).trim()) return;
        upsertUser({
            ...current,
            studentCode: current.studentCode || current.username,
        });
        setEditing(null);
        setTick((t) => t + 1);
    }

    return (
        <div>
            <div className="pageTitleRow">
                <h1 className="pageTitle">Quản lý người dùng</h1>
            </div>

            <div className="panel">
                <div style={{ fontWeight: 900, marginBottom: 10, color: '#b56b6b' }}>
                    {editing ? 'Chỉnh sửa sinh viên' : 'Thêm sinh viên'}
                </div>
                <div className="twoCol">
                    <div className="field">
                        <div className="label">Tên đăng nhập (MSSV)</div>
                        <input
                            className="input"
                            value={current.username}
                            onChange={(e) => setEditing((s) => ({ ...(s || buildNewUser()), username: e.target.value }))}
                        />
                    </div>
                    <div className="field">
                        <div className="label">Họ tên</div>
                        <input
                            className="input"
                            value={current.fullName}
                            onChange={(e) => setEditing((s) => ({ ...(s || buildNewUser()), fullName: e.target.value }))}
                        />
                    </div>
                </div>
                <div className="twoCol" style={{ marginTop: 10 }}>
                    <div className="field">
                        <div className="label">Email</div>
                        <input
                            className="input"
                            value={current.email}
                            onChange={(e) => setEditing((s) => ({ ...(s || buildNewUser()), email: e.target.value }))}
                        />
                    </div>
                    <div className="field">
                        <div className="label">Mật khẩu</div>
                        <input
                            className="input"
                            value={current.password}
                            type="text"
                            onChange={(e) => setEditing((s) => ({ ...(s || buildNewUser()), password: e.target.value }))}
                        />
                    </div>
                </div>
                <div className="actionsRow">
                    <button type="button" className="btnSmall btnSmallPrimary" onClick={save}>
                        Lưu
                    </button>
                    {editing ? (
                        <button type="button" className="btnSmall" onClick={() => setEditing(null)}>
                            Hủy
                        </button>
                    ) : null}
                </div>
            </div>

            <div style={{ height: 14 }} />

            <div className="panel">
                <div style={{ fontWeight: 900, marginBottom: 10, color: '#b56b6b' }}>Danh sách sinh viên</div>
                <div className="table">
                    {users.map((u) => (
                        <div
                            key={u.id}
                            className="rowCard"
                            style={{ gridTemplateColumns: '160px 2fr 2fr 140px 200px' }}
                        >
                            <span className="pill">{u.username}</span>
                            <div style={{ fontWeight: 900 }}>{u.fullName}</div>
                            <div style={{ fontWeight: 700 }}>{u.email}</div>
                            <div className="muted" style={{ fontWeight: 800 }}>pw: {u.password}</div>
                            <div style={{ display: 'flex', gap: 10, justifyContent: 'flex-end' }}>
                                <button type="button" className="btnSmall btnSmallPrimary" onClick={() => setEditing(u)}>
                                    Sửa
                                </button>
                                <button
                                    type="button"
                                    className="btnSmall"
                                    onClick={() => {
                                        deleteUser(u.id);
                                        setTick((t) => t + 1);
                                    }}
                                >
                                    Xóa
                                </button>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
}
