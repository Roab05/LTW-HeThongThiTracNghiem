package ltw.examsystem.service.impl;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import ltw.examsystem.dto.response.QuestionResultResponse;
import ltw.examsystem.dto.response.SubmissionDetailResponse;
import ltw.examsystem.entity.Submission;
import ltw.examsystem.service.PDFReportService;
import ltw.examsystem.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class PDFReportServiceImpl implements PDFReportService {

    @Autowired
    private SubmissionService submissionService;

    @Override
    public byte[] exportStatsToPdf(List<Submission> submissions, Map<String, Object> summaryData) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, out);

        document.open();

        // Cấu hình Font và Tiêu đề
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("BAO CAO THONG KE KET QUA THI (PDF)", fontTitle);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" "));

        // Thông tin tóm tắt
        document.add(new Paragraph("Tong so luot thi: " + summaryData.get("total")));
        document.add(new Paragraph("Diem trung binh: " + summaryData.get("average")));
        document.add(new Paragraph(" "));

        // Bảng dữ liệu
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.addCell("STT");
        table.addCell("Sinh vien");
        table.addCell("Ky thi");
        table.addCell("Diem so");
        table.addCell("Thoi gian");

        int count = 1;
        for (Submission s : submissions) {
            table.addCell(String.valueOf(count++));
            table.addCell(s.getUser().getUsername());
            table.addCell(s.getExam().getTitle());
            table.addCell(String.valueOf(s.getScore()));
            table.addCell(s.getSubmitTime().toLocalDate().toString());
        }

        document.add(table);
        document.close();
        return out.toByteArray();
    }

    @Override
    public byte[] exportIndividualResultToPdf(Long submissionId) throws IOException {
        // 1. Lấy dữ liệu chi tiết bài làm từ logic đã có
        SubmissionDetailResponse detail = submissionService.getSubmissionDetail(submissionId);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, out);
        document.open();

        // 2. Vẽ Tiêu đề và Thông tin chung
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
        Paragraph title = new Paragraph("PHIEU KET QUA BAI THI", fontTitle);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" "));

        document.add(new Paragraph("Ky thi: " + detail.getExamTitle()));
        document.add(new Paragraph("Thoi gian nop: " + detail.getSubmitTime()));
        document.add(new Paragraph("Diem so: " + detail.getScore() + "/10.0"));
        document.add(new Paragraph("-----------------------------------------------------------------------"));
        document.add(new Paragraph(" "));

        // 3. Liệt kê chi tiết từng câu hỏi (Requirement e)
        int i = 1;
        for (QuestionResultResponse q : detail.getQuestionResults()) {
            Paragraph pQuestion = new Paragraph("Cau " + i + ": " + q.getContent(),
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12));
            document.add(pQuestion);

            // Hiển thị đáp án đã chọn và đáp án đúng
            String statusText = q.getIsCorrect() ? "[Dung]" : "[Sai]";
            document.add(new Paragraph("   - Trang thai: " + statusText));
            document.add(new Paragraph("   - Giai thich: " + q.getExplanation())); // Giải thích từ DB
            document.add(new Paragraph(" "));
            i++;
        }

        document.close();
        return out.toByteArray();
    }
}