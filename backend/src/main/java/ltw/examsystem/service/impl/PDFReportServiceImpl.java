package ltw.examsystem.service.impl;

import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
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

        BaseFont bf = BaseFont.createFont("src/main/resources/fonts/times.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font titleFont = new Font(bf, 18, Font.BOLD);
        Font normalFont = new Font(bf, 13);
        Paragraph title = new Paragraph("BÁO CÁO THỐNG KÊ KẾT QUẢ THI", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" "));

        // Thông tin tóm tắt
        document.add(new Paragraph("Tổng số lượt thi: " + summaryData.get("total"), normalFont));
        document.add(new Paragraph("Điểm trung bình: " + summaryData.get("average"), normalFont));
        document.add(new Paragraph(" "));


        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.addCell("STT");
        table.addCell("Mã SV");      // Cột mới
        table.addCell("Họ và tên");  // Đổi tên cột
        table.addCell("Kỳ thi");
        table.addCell("Điểm số");
        table.addCell("Thời gian");

        int count = 1;
        for (Submission s : submissions) {
            table.addCell(String.valueOf(count++));

            // Điền Mã SV
            table.addCell(s.getUser().getStudentId() != null ? s.getUser().getStudentId() : "N/A");

            // Điền Họ và tên
            String displayName = s.getUser().getFullName() != null ? s.getUser().getFullName() : s.getUser().getUsername();
            table.addCell(displayName);

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
        BaseFont bf = BaseFont.createFont("src/main/resources/fonts/times.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font titleFont = new Font(bf, 18, Font.BOLD);
        Font normalFont = new Font(bf, 13);
        Paragraph title = new Paragraph("PHIẾU KẾT QUẢ BÀI THI", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" "));

        String studentName = detail.getFullName() != null ? detail.getFullName() : "N/A";
        String studentIdStr = detail.getStudentId() != null ? detail.getStudentId() : "N/A";

        document.add(new Paragraph("Họ và tên: " + studentName, normalFont));
        document.add(new Paragraph("Mã sinh viên: " + studentIdStr, normalFont));
        document.add(new Paragraph("Kỳ thi: " + detail.getExamTitle(), normalFont));
        document.add(new Paragraph("Thời gian nộp: " + detail.getSubmitTime(), normalFont));
        document.add(new Paragraph("Điểm số: " + detail.getScore() + "/10", normalFont));
        document.add(new Paragraph("-----------------------------------------------------------------------"));

        // 3. Liệt kê chi tiết từng câu hỏi (Requirement e)
        int i = 1;
        for (QuestionResultResponse q : detail.getQuestionResults()) {
            Paragraph pQuestion = new Paragraph("Câu " + i + ": " + q.getContent(),
                    normalFont);
            document.add(pQuestion);

            // Hiển thị đáp án đã chọn và đáp án đúng
            String statusText = q.getIsCorrect() ? "[Đúng]" : "[Sai]";
            document.add(new Paragraph("   - Trạng thái: " + statusText, normalFont));
            document.add(new Paragraph("   - Giải thích: " + q.getExplanation(), normalFont)); // Giải thích từ DB
            document.add(new Paragraph(" "));
            i++;
        }

        document.close();
        return out.toByteArray();
    }
}