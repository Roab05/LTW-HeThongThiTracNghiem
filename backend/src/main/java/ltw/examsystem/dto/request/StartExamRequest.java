package ltw.examsystem.dto.request;

import lombok.Data;

@Data
public class StartExamRequest {
    private Long examId;
    private Long userId;
}