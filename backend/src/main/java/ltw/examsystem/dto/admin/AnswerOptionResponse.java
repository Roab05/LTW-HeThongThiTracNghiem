package ltw.examsystem.dto.admin;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AnswerOptionResponse {
    private Long id;
    private String content;
    private Boolean isCorrect;
}


