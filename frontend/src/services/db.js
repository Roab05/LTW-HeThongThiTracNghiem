import { buildSeed } from './demoSeed';
import { toISODateTimeLocal } from '../utils/time';

const DB_KEY = 'ttn_demo_db_v1';
const SESSION_KEY = 'ttn_demo_session_v1';

function safeParse(json, fallback) {
    try {
        return JSON.parse(json);
    } catch {
        return fallback;
    }
}

function uuid() {
    if (typeof crypto !== 'undefined' && crypto.randomUUID) return crypto.randomUUID();
    return `id_${Math.random().toString(16).slice(2)}_${Date.now()}`;
}

function loadDb() {
    const raw = localStorage.getItem(DB_KEY);
    return raw ? safeParse(raw, null) : null;
}

function saveDb(db) {
    localStorage.setItem(DB_KEY, JSON.stringify(db));
}

export function ensureSeeded() {
    const existing = loadDb();
    if (existing?.meta?.version) return;
    saveDb(buildSeed());
}

export function getSession() {
    const raw = localStorage.getItem(SESSION_KEY);
    return raw ? safeParse(raw, null) : null;
}

export function saveSession(session) {
    localStorage.setItem(SESSION_KEY, JSON.stringify(session));
}

export function clearSession() {
    localStorage.removeItem(SESSION_KEY);
}

export function loginAdminWithPassword(username, password) {
    return username === 'admin' && password === 'admin123';
}

export function loginUserWithPassword(username, password) {
    const db = loadDb();
    const user = db?.users?.find((u) => u.username === username && u.password === password);
    return user || null;
}

export function listUsers() {
    const db = loadDb();
    return db?.users || [];
}

export function createUser({ username, fullName, email, password }) {
    const db = loadDb();
    const users = db?.users || [];
    const usernameTaken = users.some((u) => u.username.toLowerCase() === username.toLowerCase());
    if (usernameTaken) {
        return { ok: false, error: 'Tên đăng nhập đã tồn tại.' };
    }
    const emailTaken = users.some((u) => u.email.toLowerCase() === email.toLowerCase());
    if (emailTaken) {
        return { ok: false, error: 'Email đã tồn tại.' };
    }

    const user = {
        id: uuid(),
        role: 'user',
        username,
        fullName,
        email,
        password,
        studentCode: username,
    };

    const next = { ...db, users: [...users, user] };
    saveDb(next);
    return { ok: true, user };
}

export function upsertUser(user) {
    const db = loadDb();
    const users = db?.users || [];
    const idx = users.findIndex((u) => u.id === user.id);
    if (idx === -1) {
        const next = { ...db, users: [...users, { ...user, id: uuid(), role: 'user' }] };
        saveDb(next);
        return { ok: true };
    }

    const nextUsers = users.map((u) => (u.id === user.id ? { ...u, ...user } : u));
    saveDb({ ...db, users: nextUsers });
    return { ok: true };
}

export function deleteUser(userId) {
    const db = loadDb();
    const users = (db?.users || []).filter((u) => u.id !== userId);
    const attempts = (db?.attempts || []).filter((a) => a.userId !== userId);
    saveDb({ ...db, users, attempts });
}

export function listExams() {
    const db = loadDb();
    return db?.exams || [];
}

export function getExam(examId) {
    const db = loadDb();
    return (db?.exams || []).find((e) => e.id === examId) || null;
}

export function upsertExam(exam) {
    const db = loadDb();
    const exams = db?.exams || [];
    const next = { ...exam };
    if (!next.id) next.id = uuid();
    if (next.mode === 'free') {
        next.startAt = null;
        next.endAt = null;
    }

    const idx = exams.findIndex((e) => e.id === next.id);
    const nextExams = idx === -1 ? [...exams, next] : exams.map((e) => (e.id === next.id ? next : e));
    saveDb({ ...db, exams: nextExams });
    return next;
}

export function deleteExam(examId) {
    const db = loadDb();
    const exams = (db?.exams || []).filter((e) => e.id !== examId);
    const attempts = (db?.attempts || []).filter((a) => a.examId !== examId);
    saveDb({ ...db, exams, attempts });
}

export function listAttempts() {
    const db = loadDb();
    return db?.attempts || [];
}

export function getAttempt(attemptId) {
    const db = loadDb();
    return (db?.attempts || []).find((a) => a.id === attemptId) || null;
}

export function listAttemptsByUser(userId) {
    const db = loadDb();
    return (db?.attempts || []).filter((a) => a.userId === userId);
}

export function createAttempt({ examId, userId, durationMinutes }) {
    const db = loadDb();
    const attempts = db?.attempts || [];
    const now = new Date();
    const attempt = {
        id: uuid(),
        examId,
        userId,
        startedAt: now.toISOString(),
        submittedAt: null,
        durationMinutes,
        status: 'in_progress',
        answers: {},
        computed: null,
    };
    saveDb({ ...db, attempts: [...attempts, attempt] });
    return attempt;
}

export function updateAttempt(attemptId, patch) {
    const db = loadDb();
    const attempts = db?.attempts || [];
    const nextAttempts = attempts.map((a) => (a.id === attemptId ? { ...a, ...patch } : a));
    saveDb({ ...db, attempts: nextAttempts });
    return nextAttempts.find((a) => a.id === attemptId) || null;
}

export function computeAttempt(attempt, exam) {
    const total = exam.questions.length;
    let correct = 0;
    for (const q of exam.questions) {
        if (attempt.answers?.[q.id] && attempt.answers[q.id] === q.correctChoiceId) correct += 1;
    }
    const score10 = total === 0 ? 0 : Math.round((correct / total) * 10 * 100) / 100;
    return {
        correct,
        total,
        score10,
    };
}

export function finalizeAttempt({ attemptId, status }) {
    const attempt = getAttempt(attemptId);
    if (!attempt) return null;
    const exam = getExam(attempt.examId);
    if (!exam) return null;

    const computed = computeAttempt(attempt, exam);
    const now = new Date();
    return updateAttempt(attemptId, {
        submittedAt: now.toISOString(),
        status,
        computed,
    });
}

export function getExamAccessStatus(exam, now = new Date()) {
    if (exam.mode === 'free') return { key: 'ready', label: 'Sẵn sàng' };
    const start = exam.startAt ? new Date(exam.startAt) : null;
    const end = exam.endAt ? new Date(exam.endAt) : null;

    if (start && now < start) return { key: 'not_started', label: 'Chưa bắt đầu' };
    if (end && now > end) return { key: 'expired', label: 'Đã hết hạn' };
    return { key: 'ready', label: 'Sẵn sàng' };
}

export function normalizeExamForEditor(exam) {
    const next = {
        id: exam?.id || '',
        code: exam?.code || '',
        title: exam?.title || '',
        description: exam?.description || '',
        category: exam?.category || 'Luyện tập',
        mode: exam?.mode || 'free',
        durationMinutes: Number(exam?.durationMinutes || 30),
        startAtLocal: exam?.startAt ? toISODateTimeLocal(new Date(exam.startAt)) : '',
        endAtLocal: exam?.endAt ? toISODateTimeLocal(new Date(exam.endAt)) : '',
        questions: (exam?.questions || []).map((q) => ({
            id: q.id,
            text: q.text,
            correctChoiceId: q.correctChoiceId,
            explanation: q.explanation || '',
            choices: q.choices,
        })),
    };
    return next;
}
