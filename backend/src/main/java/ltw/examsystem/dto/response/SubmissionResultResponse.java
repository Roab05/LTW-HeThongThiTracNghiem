package ltw.examsystem.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubmissionResultResponse {
    private Long submissionId;
    private String examTitle;
    private Double score;
    private Integer correctAnswers;
    private Integer totalQuestions;
    private LocalDateTime submitTime;
    private String message;
}