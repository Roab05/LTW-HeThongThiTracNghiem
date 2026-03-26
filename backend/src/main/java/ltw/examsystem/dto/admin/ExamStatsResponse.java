package ltw.examsystem.dto.admin;

import lombok.Data;
import java.util.Map;

@Data
public class ExamStatsResponse {
    private Long examId;
    private String examTitle;
    private long participantsCount;
    private double averageScore;
    private double completionRate;
    private Map<String, Long> scoreDistribution;
}



