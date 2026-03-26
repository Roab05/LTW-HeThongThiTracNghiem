package ltw.examsystem.dto.admin;

import lombok.Data;

@Data
public class AnswerOptionRequest {
    private Long id;
    private String content;
    private Boolean isCorrect;
}



