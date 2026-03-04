import { useMemo, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { deleteExam, listExams } from '../../services/db';
import { formatDateTime } from '../../utils/time';

export default function AdminExamsPage() {
    const navigate = useNavigate();
    const [tick, setTick] = useState(0);
    const exams = useMemo(() => {
        void tick;
        return listExams();
    }, [tick]);

    return (
        <div>
            <div className="pageTitleRow">
                <h1 className="pageTitle">Quản lý kỳ thi</h1>
                <Link className="link" to="/admin/exams/new">
                    + Thêm kỳ thi
                </Link>
            </div>

            <div className="panel">
                <div className="table">
                    {exams.map((e) => (
                        <div
                            key={e.id}
                            className="rowCard"
                            style={{ gridTemplateColumns: '120px 2fr 180px 140px 160px' }}
                        >
                            <span className="pill">{e.code}</span>
                            <div>
                                <div style={{ fontWeight: 900 }}>{e.title}</div>
                                <div className="muted" style={{ marginTop: 4 }}>
                                    {e.mode === 'free'
                                        ? 'Tự do'
                                        : `${formatDateTime(e.startAt)} - ${formatDateTime(e.endAt)}`}
                                </div>
                            </div>
                            <div style={{ fontWeight: 800 }}>{e.category}</div>
                            <div style={{ fontWeight: 800 }}>{e.durationMinutes} phút</div>
                            <div style={{ display: 'flex', gap: 10, justifyContent: 'flex-end' }}>
                                <button
                                    type="button"
                                    className="btnSmall btnSmallPrimary"
                                    onClick={() => navigate(`/admin/exams/${e.id}/edit`)}
                                >
                                    Sửa
                                </button>
                                <button
                                    type="button"
                                    className="btnSmall"
                                    onClick={() => {
                                        deleteExam(e.id);
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
