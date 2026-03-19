package ltw.examsystem.dto.request;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter @Setter
public class SubmitExamRequest {
    private Long submissionId;
    private List<AnswerRequest> answers;
    private Boolean isAutoSubmit;
}