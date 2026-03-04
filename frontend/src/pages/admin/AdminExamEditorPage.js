import { useMemo, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import * as XLSX from 'xlsx';
import { fromISODateTimeLocal } from '../../utils/time';
import { buildQuestionsTemplateWorkbook, parseQuestionsFromExcel } from '../../utils/excelImport';
import { getExam, normalizeExamForEditor, upsertExam } from '../../services/db';

function buildEmptyQuestion() {
    return {
        id: '',
        text: '',
        correctChoiceId: 'A',
        explanation: '',
        choices: [
            { id: 'A', label: 'A', text: '' },
            { id: 'B', label: 'B', text: '' },
            { id: 'C', label: 'C', text: '' },
            { id: 'D', label: 'D', text: '' },
        ],
    };
}

export default function AdminExamEditorPage({ mode }) {
    const { examId } = useParams();
    const navigate = useNavigate();
    const original = useMemo(() => (mode === 'edit' ? getExam(examId) : null), [mode, examId]);
    const [form, setForm] = useState(() => normalizeExamForEditor(original || null));
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');

    function patch(p) {
        setForm((s) => ({ ...s, ...p }));
    }

    function validate() {
        if (!String(form.code).trim()) return 'Vui lòng nhập Mã kỳ thi.';
        if (!String(form.title).trim()) return 'Vui lòng nhập Tên kỳ thi.';
        if (Number(form.durationMinutes) <= 0) return 'Thời lượng không hợp lệ.';
        if (form.mode === 'scheduled') {
            const s = fromISODateTimeLocal(form.startAtLocal);
            const e = fromISODateTimeLocal(form.endAtLocal);
            if (!s || !e) return 'Vui lòng nhập thời gian bắt đầu/kết thúc.';
            if (s >= e) return 'Thời gian bắt đầu phải nhỏ hơn thời gian kết thúc.';
        }
        if ((form.questions || []).length === 0) return 'Vui lòng thêm ít nhất 1 câu hỏi.';
        const bad = form.questions.findIndex((q) => !String(q.text).trim());
        if (bad !== -1) return `Câu hỏi #${bad + 1} chưa có nội dung.`;
        return '';
    }

    function onSave() {
        setError('');
        setMessage('');
        const msg = validate();
        if (msg) {
            setError(msg);
            return;
        }

        const start = fromISODateTimeLocal(form.startAtLocal);
        const end = fromISODateTimeLocal(form.endAtLocal);
        const payload = {
            id: form.id || undefined,
            code: String(form.code).trim(),
            title: String(form.title).trim(),
            description: String(form.description || '').trim(),
            category: String(form.category || 'Luyện tập'),
            mode: form.mode,
            durationMinutes: Number(form.durationMinutes),
            startAt: form.mode === 'scheduled' ? start?.toISOString() : null,
            endAt: form.mode === 'scheduled' ? end?.toISOString() : null,
            questions: (form.questions || []).map((q, i) => ({
                id: q.id || `q${i + 1}`,
                text: String(q.text).trim(),
                correctChoiceId: q.correctChoiceId,
                explanation: String(q.explanation || '').trim(),
                choices: q.choices.map((c) => ({ ...c, text: String(c.text || '').trim() })),
            })),
        };

        const saved = upsertExam(payload);
        setMessage('Đã lưu kỳ thi.');
        navigate(`/admin/exams/${saved.id}/edit`, { replace: true });
    }

    async function onImportQuestions(file) {
        setError('');
        setMessage('');
        try {
            const questions = await parseQuestionsFromExcel(file);
            if (!questions.length) {
                setError('Không đọc được câu hỏi từ file.');
                return;
            }
            patch({
                questions: [
                    ...(form.questions || []),
                    ...questions.map((q, i) => ({ ...q, id: `imp_${Date.now()}_${i}` })),
                ],
            });
            setMessage(`Đã import ${questions.length} câu hỏi.`);
        } catch (e) {
            setError(`Import thất bại: ${e?.message || 'Unknown error'}`);
        }
    }

    function downloadTemplate() {
        const wb = buildQuestionsTemplateWorkbook();
        XLSX.writeFile(wb, 'questions-template.xlsx');
    }

    return (
        <div>
            <div className="pageTitleRow">
                <h1 className="pageTitle">{mode === 'edit' ? 'Chỉnh sửa kỳ thi' : 'Tạo kỳ thi'}</h1>
                <Link className="link" to="/admin/exams">
                    Quay lại
                </Link>
            </div>

            <div className="panel">
                <div className="twoCol">
                    <div>
                        <div className="field">
                            <div className="label">Mã kỳ thi</div>
                            <input className="input" value={form.code} onChange={(e) => patch({ code: e.target.value })} />
                        </div>
                        <div className="field">
                            <div className="label">Tên kỳ thi</div>
                            <input className="input" value={form.title} onChange={(e) => patch({ title: e.target.value })} />
                        </div>
                        <div className="field">
                            <div className="label">Mô tả</div>
                            <input
                                className="input"
                                value={form.description}
                                onChange={(e) => patch({ description: e.target.value })}
                                placeholder="(tuỳ chọn)"
                            />
                        </div>
                    </div>

                    <div>
                        <div className="field">
                            <div className="label">Loại</div>
                            <select className="select" value={form.mode} onChange={(e) => patch({ mode: e.target.value })}>
                                <option value="free">Tự do</option>
                                <option value="scheduled">Theo thời gian</option>
                            </select>
                        </div>
                        <div className="field">
                            <div className="label">Danh mục</div>
                            <input className="input" value={form.category} onChange={(e) => patch({ category: e.target.value })} />
                        </div>
                        <div className="field">
                            <div className="label">Thời lượng (phút)</div>
                            <input
                                className="input"
                                value={form.durationMinutes}
                                type="number"
                                min={1}
                                onChange={(e) => patch({ durationMinutes: e.target.value })}
                            />
                        </div>
                    </div>
                </div>

                {form.mode === 'scheduled' ? (
                    <div className="twoCol" style={{ marginTop: 10 }}>
                        <div className="field">
                            <div className="label">Bắt đầu</div>
                            <input
                                className="input"
                                type="datetime-local"
                                value={form.startAtLocal}
                                onChange={(e) => patch({ startAtLocal: e.target.value })}
                            />
                        </div>
                        <div className="field">
                            <div className="label">Kết thúc</div>
                            <input
                                className="input"
                                type="datetime-local"
                                value={form.endAtLocal}
                                onChange={(e) => patch({ endAtLocal: e.target.value })}
                            />
                        </div>
                    </div>
                ) : null}

                <div className="actionsRow">
                    <button type="button" className="btnSmall btnSmallPrimary" onClick={onSave}>
                        Lưu
                    </button>
                    <div style={{ flex: 1 }} />
                    <button type="button" className="btnSmall" onClick={downloadTemplate}>
                        Tải template Excel
                    </button>
                    <label className="btnSmall" style={{ cursor: 'pointer' }}>
                        Import Excel
                        <input
                            type="file"
                            accept=".xlsx,.xls"
                            style={{ display: 'none' }}
                            onChange={(e) => {
                                const f = e.target.files?.[0];
                                if (f) onImportQuestions(f);
                                e.target.value = '';
                            }}
                        />
                    </label>
                </div>

                {error ? <div className="error">{error}</div> : null}
                {message ? <div className="success">{message}</div> : null}
            </div>

            <div style={{ height: 14 }} />

            <div className="panel">
                <div style={{ fontWeight: 900, marginBottom: 10, color: '#b56b6b' }}>Danh sách câu hỏi</div>
                <div className="actionsRow" style={{ marginTop: 0 }}>
                    <button
                        type="button"
                        className="btnSmall btnSmallPrimary"
                        onClick={() => patch({ questions: [...(form.questions || []), buildEmptyQuestion()] })}
                    >
                        + Thêm câu hỏi
                    </button>
                </div>

                <div className="table" style={{ marginTop: 12 }}>
                    {(form.questions || []).map((q, qi) => (
                        <div key={qi} className="questionCard">
                            <div style={{ display: 'flex', gap: 10, alignItems: 'center' }}>
                                <div style={{ fontWeight: 900, flex: 1 }}>Câu {qi + 1}</div>
                                <button
                                    type="button"
                                    className="btnSmall"
                                    onClick={() =>
                                        patch({ questions: (form.questions || []).filter((_, i) => i !== qi) })
                                    }
                                >
                                    Xóa
                                </button>
                            </div>

                            <div className="field">
                                <div className="label">Nội dung câu hỏi</div>
                                <input
                                    className="input"
                                    value={q.text}
                                    onChange={(e) => {
                                        const next = [...form.questions];
                                        next[qi] = { ...q, text: e.target.value };
                                        patch({ questions: next });
                                    }}
                                />
                            </div>

                            <div className="twoCol">
                                <div>
                                    {q.choices.slice(0, 2).map((c, ci) => (
                                        <div key={c.id} className="field">
                                            <div className="label">Đáp án {c.label}</div>
                                            <input
                                                className="input"
                                                value={c.text}
                                                onChange={(e) => {
                                                    const next = [...form.questions];
                                                    const qq = { ...q };
                                                    const choices = qq.choices.map((x) => ({ ...x }));
                                                    choices[ci] = { ...choices[ci], text: e.target.value };
                                                    qq.choices = choices;
                                                    next[qi] = qq;
                                                    patch({ questions: next });
                                                }}
                                            />
                                        </div>
                                    ))}
                                </div>
                                <div>
                                    {q.choices.slice(2, 4).map((c, offset) => {
                                        const ci = offset + 2;
                                        return (
                                            <div key={c.id} className="field">
                                                <div className="label">Đáp án {c.label}</div>
                                                <input
                                                    className="input"
                                                    value={c.text}
                                                    onChange={(e) => {
                                                        const next = [...form.questions];
                                                        const qq = { ...q };
                                                        const choices = qq.choices.map((x) => ({ ...x }));
                                                        choices[ci] = { ...choices[ci], text: e.target.value };
                                                        qq.choices = choices;
                                                        next[qi] = qq;
                                                        patch({ questions: next });
                                                    }}
                                                />
                                            </div>
                                        );
                                    })}
                                </div>
                            </div>

                            <div className="twoCol" style={{ marginTop: 10 }}>
                                <div className="field">
                                    <div className="label">Đáp án đúng</div>
                                    <select
                                        className="select"
                                        value={q.correctChoiceId}
                                        onChange={(e) => {
                                            const next = [...form.questions];
                                            next[qi] = { ...q, correctChoiceId: e.target.value };
                                            patch({ questions: next });
                                        }}
                                    >
                                        <option value="A">A</option>
                                        <option value="B">B</option>
                                        <option value="C">C</option>
                                        <option value="D">D</option>
                                    </select>
                                </div>
                                <div className="field">
                                    <div className="label">Giải thích (tuỳ chọn)</div>
                                    <input
                                        className="input"
                                        value={q.explanation}
                                        onChange={(e) => {
                                            const next = [...form.questions];
                                            next[qi] = { ...q, explanation: e.target.value };
                                            patch({ questions: next });
                                        }}
                                    />
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
}
