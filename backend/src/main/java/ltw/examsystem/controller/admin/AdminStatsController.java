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
     * Requirement d: Lọc kết quả nâng cao theo kỳ thi, thời gian và điểm số
     */
    @GetMapping("/filter")
    public ResponseEntity<List<SubmissionHistoryResponse>> filterSubmissions(
            @RequestParam(required = false) Long examId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Double minScore,
            @RequestParam(required = false) Double maxScore) {

        // Gọi hàm lọc linh hoạt từ Repository
        List<Submission> results = submissionRepository.filterSubmissions(
                examId, startDate, endDate, minScore, maxScore);

        // Chuyển đổi sang DTO để hiển thị trên bảng thống kê của Frontend
        List<SubmissionHistoryResponse> response = results.stream().map(s -> {
            SubmissionHistoryResponse dto = new SubmissionHistoryResponse();
            dto.setSubmissionId(s.getId());
            dto.setExamTitle(s.getExam().getTitle());
            dto.setScore(s.getScore());
            dto.setSubmitTime(s.getSubmitTime());
            dto.setCorrectAnswers(s.getCorrectAnswers());
            dto.setTotalQuestions(s.getTotalQuestions());
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * Requirement d: Thống kê chi tiết theo kỳ thi (Dữ liệu cho biểu đồ)
     */
    @GetMapping("/exam/{examId}")
    public ResponseEntity<ExamStatsResponse> getExamStats(@PathVariable Long examId) {
        Exam exam = examRepository.findById(examId).orElseThrow();
        long totalStudents = userRepository.count();
        List<Submission> submissions = submissionRepository.findByExamId(examId);

        ExamStatsResponse stats = new ExamStatsResponse();
        stats.setExamId(examId);
        stats.setExamTitle(exam.getTitle());
        stats.setParticipantsCount(submissions.size());

        if (!submissions.isEmpty()) {
            double avg = submissions.stream().mapToDouble(Submission::getScore).average().orElse(0.0);
            stats.setAverageScore(Math.round(avg * 100.0) / 100.0);

            double completionRate = (double) submissions.size() / totalStudents * 100;
            stats.setCompletionRate(Math.round(completionRate * 100.0) / 100.0);

            Map<String, Long> distribution = new HashMap<>();
            distribution.put("Yếu (0-5)", submissions.stream().filter(s -> s.getScore() < 5).count());
            distribution.put("Khá (5-8)", submissions.stream().filter(s -> s.getScore() >= 5 && s.getScore() < 8).count());
            distribution.put("Giỏi (8-10)", submissions.stream().filter(s -> s.getScore() >= 8).count());
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

        // 2. Chuyển đổi thành file Excel
        byte[] excelContent = excelService.exportSubmissionsToExcel(results);

        // 3. Trả về dưới dạng file download
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

        List<Submission> results = submissionRepository.filterSubmissions(examId, startDate, endDate, minScore, maxScore);

        Map<String, Object> summary = new HashMap<>();
        summary.put("total", results.size());
        double avg = results.stream().mapToDouble(Submission::getScore).average().orElse(0.0);
        summary.put("average", Math.round(avg * 100.0) / 100.0);

        // Gọi đúng tên service mới
        byte[] pdfBytes = pdfReportService.exportStatsToPdf(results, summary);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bao_cao_ket_qua.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}