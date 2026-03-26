package ltw.examsystem.dto.student;

import lombok.Getter;
import lombok.Setter;
import ltw.examsystem.dto.admin.AnswerOptionResponse;

import java.util.List;

@Getter @Setter
public class QuestionResultResponse {
    private Long questionId;
    private String content;
    private String explanation;

    private List<AnswerOptionResponse> options;

    private Long selectedOptionId;
    private Long correctOptionId;
    private Boolean isCorrect;
}


