package ltw.examsystem.controller.admin;
import ltw.examsystem.dto.admin.DashboardStatsResponse;
import ltw.examsystem.dto.admin.ExamStatsResponse;
import ltw.examsystem.dto.admin.SubmissionHistoryResponse;
import ltw.examsystem.entity.Exam;
import ltw.examsystem.entity.Submission;
import ltw.examsystem.repository.ExamRepository;
import ltw.examsystem.repository.SubmissionRepository;
import ltw.examsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    @GetMapping("/exam-stats")
    public ResponseEntity<ExamStatsResponse> getExamStats(
            @RequestParam(required = false) Long examId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        String examTitle = "Thống kê tổng hợp";
        if (examId != null) {
            examTitle = examRepository.findById(examId)
                    .map(Exam::getTitle)
                    .orElse("Kỳ thi không tồn tại");
        }


    List<Submission> allSubmissions = submissionRepository.filterSubmissions(
            examId, startDate, endDate, null, null);
    List<Submission> validSubmissions = allSubmissions.stream()
            .filter(s -> s.getScore() != null)
            .collect(Collectors.toList());
        long totalStudents = userRepository.count();
        ExamStatsResponse stats = new ExamStatsResponse();
        stats.setExamId(examId);
        stats.setExamTitle(examTitle);
        stats.setParticipantsCount(validSubmissions.size());
        if (!validSubmissions.isEmpty()) {
             double avg = validSubmissions.stream()
                    .mapToDouble(Submission::getScore)
                    .average()
                    .orElse(0.0);
            stats.setAverageScore(Math.round(avg * 100.0) / 100.0);
             if (totalStudents > 0) {
                double rate = (double) validSubmissions.size() / totalStudents * 100;
                stats.setCompletionRate(Math.round(rate * 100.0) / 100.0);
            }
            Map<String, Long> distribution = new HashMap<>();
            distribution.put("Yếu (0-5)", validSubmissions.stream().filter(s -> s.getScore() < 5).count());
            distribution.put("Khá (5-8)", validSubmissions.stream().filter(s -> s.getScore() >= 5 && s.getScore() < 8).count());
            distribution.put("Giỏi (8-10)", validSubmissions.stream().filter(s -> s.getScore() >= 8).count());
            stats.setScoreDistribution(distribution);
        }
        return ResponseEntity.ok(stats);
    }
}


