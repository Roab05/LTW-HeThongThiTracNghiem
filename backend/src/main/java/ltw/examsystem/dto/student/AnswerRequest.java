package ltw.examsystem.dto.student;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AnswerRequest {
    private Long questionId;
    private Long selectedOptionId; // ID cÃ¡Â»Â§a Ã„â€˜ÃƒÂ¡p ÃƒÂ¡n A, B, C hoÃ¡ÂºÂ·c D mÃƒÂ  SV chÃ¡Â»Ân
}


