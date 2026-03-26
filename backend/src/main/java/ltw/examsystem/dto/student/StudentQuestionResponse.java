package ltw.examsystem.dto.student;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class StudentQuestionResponse {
    private Long id;
    private String content;
    private List<StudentAnswerOptionResponse> options;
}



