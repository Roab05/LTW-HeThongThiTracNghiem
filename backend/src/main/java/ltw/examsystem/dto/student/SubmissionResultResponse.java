package ltw.examsystem.dto.student;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SubmissionResultResponse {
    private Long submissionId;
    private String fullName;
    private String studentId;
    private String examTitle;
    private Double score;
    private Integer correctAnswers;
    private Integer totalQuestions;
    private LocalDateTime submitTime;
    private String message;
}


