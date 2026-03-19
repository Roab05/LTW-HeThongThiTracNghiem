package ltw.examsystem.dto.response;

import lombok.Data;

@Data
public class DashboardStatsResponse {
    private long totalStudents;
    private long totalExams;
    private long totalSubmissions;
    private double globalAverageScore;
}
