package ltw.examsystem.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class QuestionRequest {
    private String content;
    private String explanation;
    private List<AnswerOptionRequest> options;
}
