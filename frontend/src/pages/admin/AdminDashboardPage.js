import { useMemo } from 'react';
import { listAttempts, listExams, listUsers } from '../../services/db';

export default function AdminDashboardPage() {
    const exams = useMemo(() => listExams(), []);
    const users = useMemo(() => listUsers().filter((u) => u.role === 'user'), []);
    const attempts = useMemo(() => listAttempts(), []);

    const stats = useMemo(() => {
        const completed = attempts.filter((a) => a.status === 'submitted' || a.status === 'timeout');
        const avg =
            completed.length === 0
                ? 0
                : Math.round(
                    (completed.reduce((s, a) => s + (a.computed?.score10 || 0), 0) / completed.length) * 100
                ) / 100;
        return {
            exams: exams.length,
            users: users.length,
            attempts: attempts.length,
            avgScore: avg,
        };
    }, [exams, users, attempts]);

    return (
        <div>
            <div className="pageTitleRow">
                <h1 className="pageTitle">Dashboard</h1>
            </div>

            <div className="cardGrid">
                <div className="statCard">
                    <div className="statCardTitle">Tổng số kỳ thi</div>
                    <div className="statCardValue">{stats.exams}</div>
                </div>
                <div className="statCard">
                    <div className="statCardTitle">Tổng số sinh viên</div>
                    <div className="statCardValue">{stats.users}</div>
                </div>
                <div className="statCard">
                    <div className="statCardTitle">Tổng lượt thi</div>
                    <div className="statCardValue">{stats.attempts}</div>
                </div>
                <div className="statCard">
                    <div className="statCardTitle">Điểm TB</div>
                    <div className="statCardValue">{stats.avgScore}</div>
                </div>
            </div>

            <div className="panel">
                <div style={{ fontWeight: 900, color: '#b56b6b' }}>Gợi ý demo</div>
                <div className="muted" style={{ marginTop: 8 }}>
                    - Admin demo: <b>admin / admin123</b>
                </div>
                <div className="muted" style={{ marginTop: 6 }}>
                    - User demo: <b>230056PCNCT / 123456</b>
                </div>
            </div>
        </div>
    );
}
