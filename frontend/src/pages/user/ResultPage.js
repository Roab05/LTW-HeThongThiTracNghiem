import { useMemo } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Pie, PieChart, Cell, ResponsiveContainer, Tooltip } from 'recharts';
import { computeAttempt, getAttempt, getExam } from '../../services/db';
import { formatDateTime } from '../../utils/time';

export default function ResultPage() {
    const { attemptId } = useParams();
    const attempt = useMemo(() => getAttempt(attemptId), [attemptId]);
    const exam = useMemo(() => (attempt ? getExam(attempt.examId) : null), [attempt]);

    const computed = useMemo(() => {
        if (!attempt || !exam) return null;
        return attempt.computed || computeAttempt(attempt, exam);
    }, [attempt, exam]);

    if (!attempt || !exam || !computed) {
        return (
            <div className="panel">
                <div style={{ fontWeight: 900 }}>Không tìm thấy kết quả.</div>
                <div style={{ marginTop: 10 }}>
                    <Link className="link" to="/">
                        Quay lại
                    </Link>
                </div>
            </div>
        );
    }

    const chartData = [
        { name: 'Đúng', value: computed.correct },
        { name: 'Sai', value: computed.total - computed.correct },
    ];

    const colors = ['var(--app-success)', 'var(--app-danger)'];
    const statusLabel = attempt.status === 'timeout' ? 'Hết giờ' : 'Đã nộp';

    return (
        <div>
            <div className="pageTitleRow">
                <h1 className="pageTitle">Kết quả</h1>
                <Link className="link" to="/">
                    Về danh sách kỳ thi
                </Link>
            </div>

            <div className="twoCol">
                <div className="panel">
                    <div style={{ fontWeight: 900, fontSize: 18 }}>{exam.title}</div>
                    <div className="muted" style={{ marginTop: 6 }}>
                        Trạng thái: <b>{statusLabel}</b> — {formatDateTime(attempt.submittedAt || attempt.startedAt)}
                    </div>

                    <div style={{ display: 'flex', gap: 18, marginTop: 16, flexWrap: 'wrap' }}>
                        <div className="pill">Đúng: {computed.correct}/{computed.total}</div>
                        <div className="pill">Điểm: {computed.score10}/10</div>
                    </div>

                    <div style={{ marginTop: 16, height: 260 }}>
                        <ResponsiveContainer width="100%" height="100%">
                            <PieChart>
                                <Pie data={chartData} dataKey="value" nameKey="name" outerRadius={90}>
                                    {chartData.map((_, i) => (
                                        <Cell key={i} fill={colors[i % colors.length]} />
                                    ))}
                                </Pie>
                                <Tooltip />
                            </PieChart>
                        </ResponsiveContainer>
                    </div>
                </div>

                <div className="panel">
                    <div style={{ fontWeight: 900, marginBottom: 10 }}>Thống kê</div>
                    <div className="muted">
                        - Tỉ lệ đúng: <b>{computed.total ? Math.round((computed.correct / computed.total) * 100) : 0}%</b>
                    </div>
                    <div className="muted" style={{ marginTop: 6 }}>
                        - Số câu chưa trả lời: <b>{exam.questions.filter((q) => !attempt.answers?.[q.id]).length}</b>
                    </div>
                    <div className="muted" style={{ marginTop: 6 }}>
                        - Thời lượng: <b>{exam.durationMinutes} phút</b>
                    </div>
                </div>
            </div>

            <div style={{ height: 14 }} />

            <div className="panel">
                <div style={{ fontWeight: 900, marginBottom: 10 }}>Xem lại đáp án</div>
                <div className="table">
                    {exam.questions.map((q, i) => {
                        const selected = attempt.answers?.[q.id] || '';
                        return (
                            <div key={q.id} className="questionCard" style={{ borderRadius: 16 }}>
                                <div className="questionTitle">Câu {i + 1}: {q.text}</div>
                                <div className="choiceList">
                                    {q.choices.map((c) => {
                                        const isCorrect = c.id === q.correctChoiceId;
                                        const isSelected = c.id === selected;
                                        const bg = isCorrect ? 'var(--app-success-bg)' : isSelected ? 'var(--app-danger-bg)' : '#f7f7f7';
                                        const border = isCorrect ? 'var(--app-success)' : isSelected ? 'var(--app-danger)' : 'var(--app-border)';
                                        return (
                                            <div
                                                key={c.id}
                                                className="choice"
                                                style={{ cursor: 'default', background: bg, borderColor: border }}
                                            >
                                                <div style={{ fontWeight: 900, color: '#c63b3b' }}>{c.label}</div>
                                                <div style={{ fontWeight: 600 }}>
                                                    {c.text}{' '}
                                                    {isCorrect ? <b>(Đáp án đúng)</b> : null}
                                                    {isSelected && !isCorrect ? <b>(Bạn chọn)</b> : null}
                                                </div>
                                            </div>
                                        );
                                    })}
                                </div>
                                {q.explanation ? (
                                    <div className="muted" style={{ marginTop: 10 }}>
                                        Giải thích: {q.explanation}
                                    </div>
                                ) : null}
                            </div>
                        );
                    })}
                </div>
            </div>
        </div>
    );
}
