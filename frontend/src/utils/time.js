export function isoNow() {
    return new Date().toISOString();
}

export function addMinutes(date, minutes) {
    return new Date(date.getTime() + minutes * 60 * 1000);
}

export function pad2(n) {
    return String(n).padStart(2, '0');
}

export function formatDuration(totalSeconds) {
    const safe = Math.max(0, Math.floor(totalSeconds));
    const m = Math.floor(safe / 60);
    const s = safe % 60;
    return `${pad2(m)} : ${pad2(s)}`;
}

export function toISODateTimeLocal(date) {
    const y = date.getFullYear();
    const m = pad2(date.getMonth() + 1);
    const d = pad2(date.getDate());
    const hh = pad2(date.getHours());
    const mm = pad2(date.getMinutes());
    return `${y}-${m}-${d}T${hh}:${mm}`;
}

export function fromISODateTimeLocal(value) {
    if (!value) return null;
    const d = new Date(value);
    return Number.isNaN(d.getTime()) ? null : d;
}

export function formatDateTime(date) {
    const d = typeof date === 'string' ? new Date(date) : date;
    if (!d || Number.isNaN(d.getTime())) return '';
    return `${pad2(d.getHours())}:${pad2(d.getMinutes())} ${pad2(d.getDate())}/${pad2(
        d.getMonth() + 1
    )}/${d.getFullYear()}`;
}
