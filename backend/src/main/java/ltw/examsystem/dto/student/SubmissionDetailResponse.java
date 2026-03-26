package ltw.examsystem.dto.student;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
public class SubmissionDetailResponse {
    private Long submissionId;
    private String examTitle;
    private String fullName;
    private String studentId;
    private Double score;
    private LocalDateTime submitTime;

    private List<QuestionResultResponse> questionResults;
}


