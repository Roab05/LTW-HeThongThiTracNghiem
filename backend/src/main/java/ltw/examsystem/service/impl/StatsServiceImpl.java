package ltw.examsystem.service.impl;

import ltw.examsystem.dto.response.DashboardStatsResponse;
import ltw.examsystem.dto.response.ExamStatsResponse;
import ltw.examsystem.entity.Exam;
import ltw.examsystem.entity.Submission;
import ltw.examsystem.repository.ExamRepository;
import ltw.examsystem.repository.SubmissionRepository;
import ltw.examsystem.repository.UserRepository;
import ltw.examsystem.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatsServiceImpl implements StatsService {

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExamRepository examRepository;

    @Override
    public DashboardStatsResponse getSummary() {
        DashboardStatsResponse stats = new DashboardStatsResponse();
        stats.setTotalStudents(userRepository.count());
        stats.setTotalExams(examRepository.count());
        stats.setTotalSubmissions(submissionRepository.count());

        Double avg = submissionRepository.getGlobalAverageScore();
        stats.setGlobalAverageScore(avg != null ? Math.round(avg * 100.0) / 100.0 : 0.0);

        return stats;
    }

    @Override
    public ExamStatsResponse getExamStats(Long examId, LocalDateTime startDate, LocalDateTime endDate) {
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

        return stats;
    }
}