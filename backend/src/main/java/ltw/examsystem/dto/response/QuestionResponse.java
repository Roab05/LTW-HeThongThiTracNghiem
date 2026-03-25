package ltw.examsystem.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter @Setter
public class QuestionResponse {
    private Long id;
    private String content;
    private List<AnswerOptionResponse> options;
    private String explanation;
}