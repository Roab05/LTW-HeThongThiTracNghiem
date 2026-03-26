package ltw.examsystem.dto.admin;

import lombok.Data;

@Data
public class DashboardStatsResponse {
    private long totalStudents;
    private long totalExams;
    private long totalSubmissions;
    private double globalAverageScore;
}



