package ltw.examsystem.dto.response;

import lombok.Data;
import java.util.Map;

@Data
public class ExamStatsResponse {
    private Long examId;
    private String examTitle;
    private long participantsCount;
    private double averageScore;
    private double completionRate;
    private Map<String, Long> scoreDistribution; // Ví dụ: {"0-5": 10, "5-8": 25, "8-10": 15}
}
