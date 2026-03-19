package ltw.examsystem.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AnswerRequest {
    private Long questionId;
    private Long selectedOptionId; // ID của đáp án A, B, C hoặc D mà SV chọn
}