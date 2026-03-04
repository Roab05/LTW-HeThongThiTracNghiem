import * as XLSX from 'xlsx';

function normalizeHeader(value) {
    return String(value || '')
        .trim()
        .toLowerCase()
        .replace(/\s+/g, ' ');
}

function pick(row, keys) {
    for (const k of keys) {
        if (row[k] != null && String(row[k]).trim() !== '') return String(row[k]);
    }
    return '';
}

function toChoiceId(value) {
    const s = String(value || '').trim().toUpperCase();
    if (['A', 'B', 'C', 'D'].includes(s)) return s;
    if (['1', '2', '3', '4'].includes(s)) return ['A', 'B', 'C', 'D'][Number(s) - 1];
    return '';
}

export function buildQuestionsTemplateWorkbook() {
    const header = ['question', 'A', 'B', 'C', 'D', 'correct', 'explanation'];
    const ws = XLSX.utils.aoa_to_sheet([header]);
    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, 'Questions');
    return wb;
}

export async function parseQuestionsFromExcel(file) {
    const data = await file.arrayBuffer();
    const wb = XLSX.read(data, { type: 'array' });
    const name = wb.SheetNames[0];
    if (!name) return [];
    const sheet = wb.Sheets[name];
    const rawRows = XLSX.utils.sheet_to_json(sheet, { defval: '' });

    if (!rawRows.length) return [];

    // Map headers to normalized keys
    const rows = rawRows.map((r) => {
        const out = {};
        for (const [k, v] of Object.entries(r)) out[normalizeHeader(k)] = v;
        return out;
    });

    const questions = [];
    for (const r of rows) {
        const text = pick(r, ['question', 'câu hỏi', 'question text', 'q']);
        const a = pick(r, ['a', 'đáp án a', 'answer a']);
        const b = pick(r, ['b', 'đáp án b', 'answer b']);
        const c = pick(r, ['c', 'đáp án c', 'answer c']);
        const d = pick(r, ['d', 'đáp án d', 'answer d']);
        const correct = toChoiceId(pick(r, ['correct', 'đáp án đúng', 'answer', 'key']));
        const explanation = pick(r, ['explanation', 'giải thích', 'note']);

        if (!String(text).trim()) continue;
        questions.push({
            text: String(text).trim(),
            choices: [
                { id: 'A', label: 'A', text: String(a).trim() },
                { id: 'B', label: 'B', text: String(b).trim() },
                { id: 'C', label: 'C', text: String(c).trim() },
                { id: 'D', label: 'D', text: String(d).trim() },
            ],
            correctChoiceId: correct || 'A',
            explanation: String(explanation || '').trim(),
        });
    }

    return questions;
}
