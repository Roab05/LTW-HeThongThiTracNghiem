package ltw.examsystem.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter @Setter
public class QuestionResultResponse {
    private Long questionId;
    private String content;
    private String explanation; // Lời giải thích hiển thị sau khi nộp bài

    private List<AnswerOptionResponse> options; // Danh sách các đáp án A, B, C, D

    private Long selectedOptionId; // Đáp án sinh viên đã chọn (null nếu bỏ trống)
    private Long correctOptionId;  // Đáp án thực sự đúng của hệ thống
    private Boolean isCorrect;     // Cờ đánh dấu câu này làm đúng hay sai
}