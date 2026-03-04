import { useEffect, useMemo, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import {
    createAttempt,
    finalizeAttempt,
    getAttempt,
    getExam,
    getExamAccessStatus,
    updateAttempt,
} from '../../services/db';
import { formatDuration } from '../../utils/time';

function attemptKey(userId, examId) {
    return `ttn_active_attempt:${userId}:${examId}`;
}

export default function ExamPage() {
    const { examId } = useParams();
    const auth = useAuth();
    const navigate = useNavigate();

    const exam = useMemo(() => getExam(examId), [examId]);
    const access = useMemo(() => (exam ? getExamAccessStatus(exam) : null), [exam]);

    const [attempt, setAttempt] = useState(null);
    const [idx, setIdx] = useState(0);
    const [remaining, setRemaining] = useState(0);
    const [error, setError] = useState('');

    useEffect(() => {
        if (!exam) return;

        if (exam.mode === 'scheduled' && access?.key !== 'ready') {
            return;
        }

        const key = attemptKey(auth.session.userId, exam.id);
        const existingId = sessionStorage.getItem(key);
        const existing = existingId ? getAttempt(existingId) : null;
        const usable = existing && existing.status === 'in_progress' ? existing : null;

        const nextAttempt = usable ||
            createAttempt({
                examId: exam.id,
                userId: auth.session.userId,
                durationMinutes: exam.durationMinutes,
            });

        sessionStorage.setItem(key, nextAttempt.id);
        setAttempt(nextAttempt);

        const startedAt = new Date(nextAttempt.startedAt).getTime();
        const totalSeconds = exam.durationMinutes * 60;
        const elapsed = Math.floor((Date.now() - startedAt) / 1000);
        setRemaining(Math.max(0, totalSeconds - elapsed));
    }, [exam, access, auth.session.userId]);

    useEffect(() => {
        if (!exam || !attempt) return;

        const startedAt = new Date(attempt.startedAt).getTime();
        const totalSeconds = exam.durationMinutes * 60;

        const t = setInterval(() => {
            const elapsed = Math.floor((Date.now() - startedAt) / 1000);
            const left = Math.max(0, totalSeconds - elapsed);
            setRemaining(left);
            if (left <= 0) {
                clearInterval(t);
                const finalized = finalizeAttempt({ attemptId: attempt.id, status: 'timeout' });
                const key = attemptKey(auth.session.userId, exam.id);
                sessionStorage.removeItem(key);
                navigate(`/results/${finalized.id}`, { replace: true });
            }
        }, 1000);

        return () => clearInterval(t);
    }, [exam, attempt, auth.session.userId, navigate]);

    if (!exam) {
        return (
            <div className="panel">
                <div style={{ fontWeight: 900 }}>Không tìm thấy bài thi.</div>
            </div>
        );
    }

    if (exam.mode === 'scheduled' && access?.key !== 'ready') {
        return (
            <div className="panel">
                <div style={{ fontWeight: 900, marginBottom: 8 }}>Bài thi chưa sẵn sàng.</div>
                <div className="muted">Trạng thái: {access?.label}</div>
                <div className="actionsRow">
                    <button type="button" className="btnSmall btnSmallPrimary" onClick={() => navigate('/')}
                    >
                        Quay lại
                    </button>
                </div>
            </div>
        );
    }

    const q = exam.questions[idx];
    const answers = attempt?.answers || {};

    function choose(choiceId) {
        if (!attempt) return;
        setError('');
        const nextAnswers = { ...answers, [q.id]: choiceId };
        setAttempt((a) => ({ ...a, answers: nextAnswers }));
        updateAttempt(attempt.id, { answers: nextAnswers });
    }

    function submit() {
        if (!attempt) return;
        setError('');
        const finalized = finalizeAttempt({ attemptId: attempt.id, status: 'submitted' });
        const key = attemptKey(auth.session.userId, exam.id);
        sessionStorage.removeItem(key);
        navigate(`/results/${finalized.id}`);
    }

    return (
        <div className="examShell">
            <header className="examHeader">
                <div>
                    <div className="examHeaderTitle">Bài thi: {exam.title}</div>
                    <div className="examHeaderMeta">Môn học: {exam.category}</div>
                </div>
                <div className="timerBox">
                    <div style={{ opacity: 0.92 }}>Thời gian còn lại</div>
                    <div className="timerValue">{formatDuration(remaining)}</div>
                </div>
            </header>

            <div className="examBody">
                <section className="questionCard">
                    <div className="questionTitle">Câu {idx + 1}: {q.text}</div>

                    <div className="choiceList">
                        {q.choices.map((c) => {
                            const active = answers[q.id] === c.id;
                            return (
                                <div
                                    key={c.id}
                                    className={`choice ${active ? 'choiceActive' : ''}`}
                                    role="button"
                                    tabIndex={0}
                                    onClick={() => choose(c.id)}
                                    onKeyDown={(e) => {
                                        if (e.key === 'Enter' || e.key === ' ') choose(c.id);
                                    }}
                                >
                                    <div style={{ fontWeight: 900, color: '#c63b3b' }}>{c.label}</div>
                                    <div style={{ fontWeight: 600 }}>{c.text}</div>
                                </div>
                            );
                        })}
                    </div>

                    {error ? <div className="error">{error}</div> : null}

                    <div className="actionsRow">
                        <button
                            type="button"
                            className="btnSmall"
                            disabled={idx === 0}
                            onClick={() => setIdx((i) => Math.max(0, i - 1))}
                        >
                            Trước
                        </button>
                        <button
                            type="button"
                            className="btnSmall"
                            disabled={idx >= exam.questions.length - 1}
                            onClick={() => setIdx((i) => Math.min(exam.questions.length - 1, i + 1))}
                        >
                            Sau
                        </button>
                        <div style={{ flex: 1 }} />
                        <button type="button" className="btnSmall btnSmallPrimary" onClick={submit}>
                            Nộp bài
                        </button>
                    </div>
                </section>

                <aside className="questionNav">
                    <div className="questionNavTitle">Danh sách câu hỏi</div>
                    <div className="qGrid">
                        {exam.questions.map((qq, i) => {
                            const answered = Boolean(answers[qq.id]);
                            const active = i === idx;
                            return (
                                <button
                                    type="button"
                                    key={qq.id}
                                    className={`qCell ${answered ? 'qCellAnswered' : ''} ${active ? 'qCellActive' : ''}`}
                                    onClick={() => setIdx(i)}
                                >
                                    {i + 1}
                                </button>
                            );
                        })}
                    </div>
                </aside>
            </div>
        </div>
    );
}
