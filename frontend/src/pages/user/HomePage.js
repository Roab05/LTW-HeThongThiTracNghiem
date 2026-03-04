import { useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { getExamAccessStatus, listAttemptsByUser, listExams } from '../../services/db';
import { formatDateTime } from '../../utils/time';

export default function HomePage() {
    const auth = useAuth();
    const navigate = useNavigate();
    const now = useMemo(() => new Date(), []);

    const [query, setQuery] = useState('');
    const [status, setStatus] = useState('all');
    const [category, setCategory] = useState('all');

    const exams = useMemo(() => listExams(), []);
    const attempts = useMemo(() => listAttemptsByUser(auth.session.userId), [auth.session.userId]);

    const categories = useMemo(() => {
        const set = new Set(exams.map((e) => e.category));
        return ['all', ...Array.from(set)];
    }, [exams]);

    const enriched = useMemo(() => {
        return exams
            .map((e) => {
                const access = getExamAccessStatus(e, now);
                return { ...e, access };
            })
            .filter((e) => {
                const q = query.trim().toLowerCase();
                if (!q) return true;
                return (
                    e.title.toLowerCase().includes(q) ||
                    e.code.toLowerCase().includes(q) ||
                    e.category.toLowerCase().includes(q)
                );
            })
            .filter((e) => (status === 'all' ? true : e.access.key === status))
            .filter((e) => (category === 'all' ? true : e.category === category));
    }, [exams, query, status, category, now]);

    const counts = useMemo(() => {
        const items = exams.map((e) => getExamAccessStatus(e, now).key);
        return {
            total: exams.length,
            ready: items.filter((k) => k === 'ready').length,
            not_started: items.filter((k) => k === 'not_started').length,
            expired: items.filter((k) => k === 'expired').length,
        };
    }, [exams, now]);

    const recentAttempts = useMemo(() => {
        return [...attempts]
            .filter((a) => a.status !== 'in_progress')
            .sort((a, b) => new Date(b.submittedAt || 0) - new Date(a.submittedAt || 0))
            .slice(0, 5)
            .map((a) => {
                const exam = exams.find((e) => e.id === a.examId);
                const score = a.computed?.score10 ?? '-';
                return {
                    id: a.id,
                    examTitle: exam?.title || a.examId,
                    status: a.status,
                    submittedAt: a.submittedAt,
                    score,
                };
            });
    }, [attempts, exams]);

    return (
        <div>
            <div className="pageTitleRow">
                <h1 className="pageTitle">Danh sách kì thi</h1>
            </div>

            <div className="toolbar">
                <input
                    className="input"
                    value={query}
                    onChange={(e) => setQuery(e.target.value)}
                    placeholder="Tìm kiếm theo tên hoặc mã"
                />
                <select className="select" value={status} onChange={(e) => setStatus(e.target.value)}>
                    <option value="all">Tất cả</option>
                    <option value="ready">Sẵn sàng</option>
                    <option value="not_started">Chưa bắt đầu</option>
                    <option value="expired">Đã hết hạn</option>
                </select>
                <select className="select" value={category} onChange={(e) => setCategory(e.target.value)}>
                    {categories.map((c) => (
                        <option key={c} value={c}>
                            {c === 'all' ? 'Tất cả' : c}
                        </option>
                    ))}
                </select>
            </div>

            <div className="cardGrid">
                <div className="statCard">
                    <div className="statCardTitle">Tổng số bài thi</div>
                    <div className="statCardValue">{counts.total}</div>
                </div>
                <div className="statCard">
                    <div className="statCardTitle">Sẵn sàng</div>
                    <div className="statCardValue">{counts.ready}</div>
                </div>
                <div className="statCard">
                    <div className="statCardTitle">Chưa bắt đầu</div>
                    <div className="statCardValue">{counts.not_started}</div>
                </div>
                <div className="statCard">
                    <div className="statCardTitle">Đã hết hạn</div>
                    <div className="statCardValue">{counts.expired}</div>
                </div>
            </div>

            <div className="table">
                {enriched.map((e) => {
                    const isReady = e.access.key === 'ready';
                    const isExpired = e.access.key === 'expired';
                    const buttonLabel = isExpired ? 'Quá hạn' : 'Bắt đầu';
                    return (
                        <div key={e.id} className="rowCard">
                            <span className="pill">{e.code}</span>
                            <div>
                                <div style={{ fontWeight: 900 }}>{e.title}</div>
                                <div className="muted" style={{ marginTop: 4 }}>
                                    {e.mode === 'free'
                                        ? 'Không giới hạn'
                                        : `${formatDateTime(e.startAt)} - ${formatDateTime(e.endAt)}`}
                                </div>
                            </div>
                            <div style={{ fontWeight: 800 }}>{e.category}</div>
                            <div style={{ fontWeight: 800 }}>{e.durationMinutes} phút</div>
                            <button
                                type="button"
                                className={`btnSmall ${isExpired ? '' : 'btnSmallPrimary'}`}
                                disabled={!isReady}
                                onClick={() => navigate(`/exams/${e.id}`)}
                            >
                                {buttonLabel}
                            </button>
                        </div>
                    );
                })}
            </div>

            <div style={{ height: 18 }} />

            <div className="panel">
                <div style={{ fontWeight: 900, marginBottom: 10, color: '#b56b6b' }}>Kết quả gần đây</div>
                {recentAttempts.length === 0 ? (
                    <div className="muted">Chưa có bài thi nào được nộp.</div>
                ) : (
                    <div className="table">
                        {recentAttempts.map((a) => (
                            <div
                                key={a.id}
                                className="rowCard"
                                style={{ gridTemplateColumns: '2fr 160px 220px 140px 120px' }}
                            >
                                <div style={{ fontWeight: 900 }}>{a.examTitle}</div>
                                <span className="pill">{a.status}</span>
                                <div style={{ fontWeight: 800 }}>{formatDateTime(a.submittedAt)}</div>
                                <div style={{ fontWeight: 900 }}>Điểm: {a.score}</div>
                                <button
                                    type="button"
                                    className="btnSmall btnSmallPrimary"
                                    onClick={() => navigate(`/results/${a.id}`)}
                                >
                                    Xem
                                </button>
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
}
