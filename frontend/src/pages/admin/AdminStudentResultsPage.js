import { useCallback, useMemo, useState } from 'react';
import { computeAttempt, getExam, listAttempts, listUsers } from '../../services/db';
import { exportToExcel, exportToPdf } from '../../utils/exporting';

export default function AdminStudentResultsPage() {
    const users = useMemo(() => listUsers().filter((u) => u.role === 'user'), []);
    const attempts = useMemo(() => listAttempts().filter((a) => a.status !== 'in_progress'), []);
    const exams = useMemo(() => new Map(), []);

    const [q, setQ] = useState('');
    const [selectedUserId, setSelectedUserId] = useState('');

    const matched = useMemo(() => {
        const needle = q.trim().toLowerCase();
        const base = needle
            ? users.filter((u) =>
                String(u.fullName).toLowerCase().includes(needle) ||
                String(u.studentCode || u.username).toLowerCase().includes(needle)
            )
            : users;
        return base.slice(0, 20);
    }, [q, users]);

    const selected = useMemo(() => users.find((u) => u.id === selectedUserId) || null, [users, selectedUserId]);

    const userAttempts = useMemo(() => {
        if (!selected) return [];
        return attempts
            .filter((a) => a.userId === selected.id)
            .sort((a, b) => new Date(b.submittedAt || 0) - new Date(a.submittedAt || 0));
    }, [attempts, selected]);

    const getExamCached = useCallback((examId) => {
        if (exams.has(examId)) return exams.get(examId);
        const ex = getExam(examId);
        exams.set(examId, ex);
        return ex;
    }, [exams]);

    const rows = useMemo(() => {
        return userAttempts.map((a) => {
            const ex = getExamCached(a.examId);
            const computed = a.computed || (ex ? computeAttempt(a, ex) : { score10: 0, correct: 0, total: 0 });
            return {
                attemptId: a.id,
                examTitle: ex?.title || a.examId,
                status: a.status,
                correct: computed.correct,
                total: computed.total,
                score10: computed.score10,
            };
        });
    }, [userAttempts, getExamCached]);

    function exportExcel() {
        if (!selected) return;
        exportToExcel({
            fileName: `ket-qua-${selected.studentCode || selected.username}.xlsx`,
            sheetName: 'Results',
            columns: [
                { key: 'examTitle', label: 'Kỳ thi' },
                { key: 'status', label: 'Trạng thái' },
                { key: 'correct', label: 'Đúng' },
                { key: 'total', label: 'Tổng' },
                { key: 'score10', label: 'Điểm (10)' },
            ],
            rows,
        });
    }

    function exportPdf() {
        if (!selected) return;
        exportToPdf({
            fileName: `ket-qua-${selected.studentCode || selected.username}.pdf`,
            title: `Kết quả sinh viên: ${selected.fullName} (${selected.studentCode || selected.username})`,
            columns: [
                { key: 'examTitle', label: 'Kỳ thi' },
                { key: 'status', label: 'Trạng thái' },
                { key: 'correct', label: 'Đúng' },
                { key: 'total', label: 'Tổng' },
                { key: 'score10', label: 'Điểm (10)' },
            ],
            rows,
        });
    }

    return (
        <div>
            <div className="pageTitleRow">
                <h1 className="pageTitle">Kết quả từng sinh viên</h1>
            </div>

            <div className="panel">
                <div className="toolbar" style={{ gridTemplateColumns: '1fr 240px 240px' }}>
                    <input
                        className="input"
                        value={q}
                        onChange={(e) => setQ(e.target.value)}
                        placeholder="Tìm theo tên hoặc MSSV"
                    />
                    <select className="select" value={selectedUserId} onChange={(e) => setSelectedUserId(e.target.value)}>
                        <option value="">Chọn sinh viên...</option>
                        {matched.map((u) => (
                            <option key={u.id} value={u.id}>
                                {u.fullName} ({u.studentCode || u.username})
                            </option>
                        ))}
                    </select>
                    <div style={{ display: 'flex', gap: 10 }}>
                        <button type="button" className="btnSmall btnSmallPrimary" disabled={!selected} onClick={exportExcel}>
                            Xuất Excel
                        </button>
                        <button type="button" className="btnSmall" disabled={!selected} onClick={exportPdf}>
                            Xuất PDF
                        </button>
                    </div>
                </div>

                {!selected ? (
                    <div className="muted">Chọn sinh viên để xem kết quả.</div>
                ) : (
                    <div>
                        <div style={{ fontWeight: 900, marginBottom: 10 }}>
                            {selected.fullName} — {selected.studentCode || selected.username}
                        </div>
                        <div className="table">
                            {rows.map((r) => (
                                <div
                                    key={r.attemptId}
                                    className="rowCard"
                                    style={{ gridTemplateColumns: '2fr 140px 140px 120px 120px' }}
                                >
                                    <div style={{ fontWeight: 900 }}>{r.examTitle}</div>
                                    <span className="pill">{r.status}</span>
                                    <div style={{ fontWeight: 900 }}>
                                        {r.correct}/{r.total}
                                    </div>
                                    <div style={{ fontWeight: 900 }}>Điểm: {r.score10}</div>
                                    <button
                                        type="button"
                                        className="btnSmall"
                                        onClick={() => window.print()}
                                    >
                                        In
                                    </button>
                                </div>
                            ))}
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}
