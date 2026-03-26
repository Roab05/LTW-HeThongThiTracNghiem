package ltw.examsystem.service;

import ltw.examsystem.entity.Submission;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface PDFReportService {
    // Xuất báo cáo thống kê tổng hợp cho Admin dưới dạng PDF
    byte[] exportStatsToPdf(Long examId, LocalDateTime startDate, LocalDateTime endDate, Double minScore, Double maxScore) throws IOException;

    // MỚI: Xuất phiếu điểm cá nhân cho một lần thi (Requirement e)
    byte[] exportIndividualResultToPdf(Long submissionId) throws IOException;
}