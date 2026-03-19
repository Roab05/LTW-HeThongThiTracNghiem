package ltw.examsystem.service;

import ltw.examsystem.entity.Submission;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface PDFReportService {
    // Xuất báo cáo thống kê tổng hợp cho Admin dưới dạng PDF
    byte[] exportStatsToPdf(List<Submission> submissions, Map<String, Object> summaryData) throws IOException;

    // MỚI: Xuất phiếu điểm cá nhân cho một lần thi (Requirement e)
    byte[] exportIndividualResultToPdf(Long submissionId) throws IOException;
}