package ltw.examsystem.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class StudentAnswerOptionResponse {
    private Long id;
    private String content;
    // Tuyệt đối không có trường isCorrect ở đây!
}