import { useMemo, useState } from 'react';
import {
    Bar,
    BarChart,
    CartesianGrid,
    Legend,
    ResponsiveContainer,
    Tooltip,
    XAxis,
    YAxis,
} from 'recharts';
import { listAttempts, listExams, listUsers } from '../../services/db';
import { exportToExcel, exportToPdf } from '../../utils/exporting';

function bucket(score10) {
    if (score10 < 2) return '0-2';
    if (score10 < 4) return '2-4';
    if (score10 < 6) return '4-6';
    if (score10 < 8) return '6-8';
    return '8-10';
}

export default function AdminStatisticsPage() {
    const exams = useMemo(() => listExams(), []);
    const users = useMemo(() => listUsers().filter((u) => u.role === 'user'), []);
    const attempts = useMemo(() => listAttempts(), []);

    const [examId, setExamId] = useState('all');
    const [from, setFrom] = useState('');
    const [to, setTo] = useState('');

    const filtered = useMemo(() => {
        const fromDate = from ? new Date(from) : null;
        const toDate = to ? new Date(to) : null;
        return attempts
            .filter((a) => a.status !== 'in_progress')
            .filter((a) => (examId === 'all' ? true : a.examId === examId))
            .filter((a) => {
                const d = a.submittedAt ? new Date(a.submittedAt) : null;
                if (!d) return false;
                if (fromDate && d < fromDate) return false;
                if (toDate && d > toDate) return false;
                return true;
            });
    }, [attempts, examId, from, to]);

    const tableRows = useMemo(() => {
        return filtered.map((a) => {
            const exam = exams.find((e) => e.id === a.examId);
            const user = users.find((u) => u.id === a.userId);
            return {
                attemptId: a.id,
                studentCode: user?.studentCode || user?.username || a.userId,
                fullName: user?.fullName || '-',
                examCode: exam?.code || a.examId,
                examTitle: exam?.title || '-',
                status: a.status,
                score10: a.computed?.score10 ?? 0,
            };
        });
    }, [filtered, exams, users]);

    const dist = useMemo(() => {
        const map = new Map([
            ['0-2', 0],
            ['2-4', 0],
            ['4-6', 0],
            ['6-8', 0],
            ['8-10', 0],
        ]);
        for (const r of tableRows) {
            const b = bucket(Number(r.score10 || 0));
            map.set(b, (map.get(b) || 0) + 1);
        }
        return Array.from(map.entries()).map(([range, count]) => ({ range, count }));
    }, [tableRows]);

    const summary = useMemo(() => {
        const total = tableRows.length;
        const avg = total === 0 ? 0 : Math.round((tableRows.reduce((s, r) => s + Number(r.score10 || 0), 0) / total) * 100) / 100;
        const completedUsers = new Set(tableRows.map((r) => r.studentCode)).size;
        const completionRate = users.length === 0 ? 0 : Math.round((completedUsers / users.length) * 100);
        return { total, avg, completionRate };
    }, [tableRows, users.length]);

    function exportExcel() {
        exportToExcel({
            fileName: 'thong-ke.xlsx',
            sheetName: 'Statistics',
            columns: [
                { key: 'studentCode', label: 'MSSV' },
                { key: 'fullName', label: 'Họ tên' },
                { key: 'examCode', label: 'Mã kỳ thi' },
                { key: 'examTitle', label: 'Tên kỳ thi' },
                { key: 'status', label: 'Trạng thái' },
                { key: 'score10', label: 'Điểm (10)' },
            ],
            rows: tableRows,
        });
    }

    function exportPdf() {
        exportToPdf({
            fileName: 'thong-ke.pdf',
            title: 'Thống kê tổng hợp kết quả',
            columns: [
                { key: 'studentCode', label: 'MSSV' },
                { key: 'fullName', label: 'Họ tên' },
                { key: 'examCode', label: 'Mã kỳ thi' },
                { key: 'examTitle', label: 'Tên kỳ thi' },
                { key: 'status', label: 'Trạng thái' },
                { key: 'score10', label: 'Điểm (10)' },
            ],
            rows: tableRows,
        });
    }

    return (
        <div>
            <div className="pageTitleRow">
                <h1 className="pageTitle">Thống kê</h1>
            </div>

            <div className="panel">
                <div className="toolbar" style={{ gridTemplateColumns: '1fr 1fr 1fr' }}>
                    <select className="select" value={examId} onChange={(e) => setExamId(e.target.value)}>
                        <option value="all">Tất cả kỳ thi</option>
                        {exams.map((e) => (
                            <option key={e.id} value={e.id}>
                                {e.title}
                            </option>
                        ))}
                    </select>
                    <input className="input" type="date" value={from} onChange={(e) => setFrom(e.target.value)} />
                    <input className="input" type="date" value={to} onChange={(e) => setTo(e.target.value)} />
                </div>

                <div className="actionsRow" style={{ marginTop: 0 }}>
                    <button type="button" className="btnSmall btnSmallPrimary" onClick={exportExcel}>
                        Xuất Excel
                    </button>
                    <button type="button" className="btnSmall" onClick={exportPdf}>
                        Xuất PDF
                    </button>
                </div>

                <div className="cardGrid" style={{ gridTemplateColumns: 'repeat(3, minmax(0, 1fr))' }}>
                    <div className="statCard">
                        <div className="statCardTitle">Tổng lượt thi</div>
                        <div className="statCardValue">{summary.total}</div>
                    </div>
                    <div className="statCard">
                        <div className="statCardTitle">Điểm TB</div>
                        <div className="statCardValue">{summary.avg}</div>
                    </div>
                    <div className="statCard">
                        <div className="statCardTitle">Tỉ lệ tham gia</div>
                        <div className="statCardValue">{summary.completionRate}%</div>
                    </div>
                </div>

                <div style={{ height: 280 }}>
                    <ResponsiveContainer width="100%" height="100%">
                        <BarChart data={dist} margin={{ top: 10, right: 30, left: 0, bottom: 0 }}>
                            <CartesianGrid strokeDasharray="3 3" />
                            <XAxis dataKey="range" />
                            <YAxis allowDecimals={false} />
                            <Tooltip />
                            <Legend />
                            <Bar dataKey="count" name="Số lượt" fill="var(--app-red)" />
                        </BarChart>
                    </ResponsiveContainer>
                </div>
            </div>
        </div>
    );
}
