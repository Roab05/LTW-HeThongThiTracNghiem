package ltw.examsystem.service;

import ltw.examsystem.dto.response.DashboardStatsResponse;
import ltw.examsystem.dto.response.ExamStatsResponse;

import java.time.LocalDateTime;

public interface StatsService {

    // Thống kê tổng quan cho Dashboard
    DashboardStatsResponse getSummary();

    // Thống kê chi tiết theo kỳ thi hoặc theo thời gian
    ExamStatsResponse getExamStats(Long examId, LocalDateTime startDate, LocalDateTime endDate);
}