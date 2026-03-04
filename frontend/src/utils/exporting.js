import * as XLSX from 'xlsx';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';

export function exportToExcel({ fileName, sheetName, columns, rows }) {
    const header = columns.map((c) => c.label);
    const data = rows.map((r) => columns.map((c) => r[c.key]));
    const ws = XLSX.utils.aoa_to_sheet([header, ...data]);
    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, sheetName || 'Sheet1');
    XLSX.writeFile(wb, fileName.endsWith('.xlsx') ? fileName : `${fileName}.xlsx`);
}

export function exportToPdf({ fileName, title, columns, rows }) {
    const doc = new jsPDF({ orientation: 'landscape', unit: 'pt', format: 'a4' });
    const marginLeft = 40;
    doc.setFontSize(14);
    if (title) doc.text(title, marginLeft, 40);

    autoTable(doc, {
        startY: title ? 60 : 30,
        head: [columns.map((c) => c.label)],
        body: rows.map((r) => columns.map((c) => String(r[c.key] ?? ''))),
        styles: { fontSize: 9 },
        margin: { left: marginLeft, right: marginLeft },
    });

    doc.save(fileName.endsWith('.pdf') ? fileName : `${fileName}.pdf`);
}
