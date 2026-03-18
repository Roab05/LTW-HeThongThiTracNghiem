package ltw.examsystem.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter @Setter
public class ExamDetailResponse {
    private Long id;
    private String title;
    private Integer durationMinutes;
    private List<QuestionResponse> questions;
}