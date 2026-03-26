package ltw.examsystem.controller.admin;

import ltw.examsystem.dto.response.DashboardStatsResponse;
import ltw.examsystem.dto.response.ExamStatsResponse;
import ltw.examsystem.dto.response.SubmissionHistoryResponse;
import ltw.examsystem.entity.Exam;
import ltw.examsystem.entity.Submission;
import ltw.examsystem.repository.ExamRepository;
import ltw.examsystem.repository.SubmissionRepository;
import ltw.examsystem.repository.UserRepository;
import ltw.examsystem.service.ExcelService;
import ltw.examsystem.service.PDFReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/admin/stats")
public class AdminStatsController {

    @Autowired
    private SubmissionRepository submissionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ExamRepository examRepository;
    @Autowired
    private ExcelService excelService;
    @Autowired
    private PDFReportService pdfReportService;

    /**
     * Requirement d: Thống kê tổng quan cho Dashboard
     */
    @GetMapping("/summary")
    public ResponseEntity<DashboardStatsResponse> getSummary() {
        DashboardStatsResponse stats = new DashboardStatsResponse();
        stats.setTotalStudents(userRepository.count());
        stats.setTotalExams(examRepository.count());
        stats.setTotalSubmissions(submissionRepository.count());

        Double avg = submissionRepository.getGlobalAverageScore();
        stats.setGlobalAverageScore(avg != null ? Math.round(avg * 100.0) / 100.0 : 0.0);

        return ResponseEntity.ok(stats);
    }

    /**
     * Requirement d: Thống kê chi tiết theo kỳ thi (Dữ liệu cho biểu đồ)
     */
    @GetMapping("/exam-stats") // Đổi URL để linh hoạt hơn (không bắt buộc id trong path nếu muốn lọc rộng)
    public ResponseEntity<ExamStatsResponse> getExamStats(
            @RequestParam(required = false) Long examId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        // 1. Lấy thông tin kỳ thi (nếu có examId) để hiển thị tiêu đề báo cáo
        String examTitle = "Thống kê tổng hợp";
        if (examId != null) {
            examTitle = examRepository.findById(examId)
                    .map(Exam::getTitle)
                    .orElse("Kỳ thi không tồn tại");
        }

        // 2. Tái sử dụng hàm lọc linh hoạt từ Repository (đã có sẵn trong project của bạn)
        // Truyền minScore/maxScore là null vì ở đây ta chỉ cần lọc theo thời gian/kỳ thi
        List<Submission> allSubmissions = submissionRepository.filterSubmissions(
                examId, startDate, endDate, null, null);

        // 3. Lọc bỏ các bản ghi chưa có điểm để tránh NullPointerException khi tính toán
        List<Submission> validSubmissions = allSubmissions.stream()
                .filter(s -> s.getScore() != null)
                .collect(Collectors.toList());

        long totalStudents = userRepository.count();
        ExamStatsResponse stats = new ExamStatsResponse();
        stats.setExamId(examId);
        stats.setExamTitle(examTitle);
        stats.setParticipantsCount(validSubmissions.size());

        if (!validSubmissions.isEmpty()) {
            // Tính điểm trung bình an toàn
            double avg = validSubmissions.stream()
                    .mapToDouble(Submission::getScore)
                    .average()
                    .orElse(0.0);
            stats.setAverageScore(Math.round(avg * 100.0) / 100.0);

            // Tính tỷ lệ hoàn thành dựa trên tổng số sinh viên hệ thống
            if (totalStudents > 0) {
                double rate = (double) validSubmissions.size() / totalStudents * 100;
                stats.setCompletionRate(Math.round(rate * 100.0) / 100.0);
            }

            // Tính biểu đồ phân bố điểm
            Map<String, Long> distribution = new HashMap<>();
            distribution.put("Yếu (0-5)", validSubmissions.stream().filter(s -> s.getScore() < 5).count());
            distribution.put("Khá (5-8)", validSubmissions.stream().filter(s -> s.getScore() >= 5 && s.getScore() < 8).count());
            distribution.put("Giỏi (8-10)", validSubmissions.stream().filter(s -> s.getScore() >= 8).count());
            stats.setScoreDistribution(distribution);
        }

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/export-excel")
    public ResponseEntity<byte[]> exportToExcel(
            @RequestParam(required = false) Long examId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Double minScore,
            @RequestParam(required = false) Double maxScore) throws IOException {

        // 1. Lấy dữ liệu đã lọc (Tái sử dụng logic lọc của Requirement d)
        List<Submission> results = submissionRepository.filterSubmissions(
                examId, startDate, endDate, minScore, maxScore);

        // BỔ SUNG: Lọc bỏ những bài nộp có điểm bị null
        List<Submission> validResults = results.stream()
                .filter(s -> s.getScore() != null)
                .collect(Collectors.toList());

        // 2. Chuyển đổi thành file Excel (Dùng list đã lọc)
        byte[] excelContent = excelService.exportSubmissionsToExcel(validResults);

        // 3. Trả về file download
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bao_cao_ket_qua.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelContent);
    }

    @GetMapping("/export-pdf")
    public ResponseEntity<byte[]> exportToPdf(
            @RequestParam(required = false) Long examId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Double minScore,
            @RequestParam(required = false) Double maxScore) throws IOException {

        // 1. Lấy dữ liệu thô từ Repository
        List<Submission> allResults = submissionRepository.filterSubmissions(examId, startDate, endDate, minScore, maxScore);

        // 2. ĐÃ SỬA: Lọc bỏ những bài nộp có điểm bị null (chưa hoàn thành)
        List<Submission> validResults = allResults.stream()
                .filter(s -> s.getScore() != null)
                .collect(Collectors.toList());

        // 3. Tính toán các thông số tóm tắt (Summary Data) trên dữ liệu sạch
        Map<String, Object> summary = new HashMap<>();
        summary.put("total", validResults.size());

        // ĐÃ SỬA: Tính trung bình an toàn
        double avg = validResults.stream()
                .mapToDouble(Submission::getScore)
                .average()
                .orElse(0.0);
        summary.put("average", Math.round(avg * 100.0) / 100.0);

        // 4. Gọi Service xuất PDF với dữ liệu đã được làm sạch
        byte[] pdfBytes = pdfReportService.exportStatsToPdf(validResults, summary);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bao_cao_ket_qua.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}