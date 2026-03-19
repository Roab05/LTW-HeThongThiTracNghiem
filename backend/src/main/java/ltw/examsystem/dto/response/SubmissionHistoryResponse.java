package ltw.examsystem.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter
public class SubmissionHistoryResponse {
    private Long submissionId;
    private String examTitle;
    private LocalDateTime submitTime;
    private Double score;
    private Integer correctAnswers;
    private Integer totalQuestions;
    private String status;
}