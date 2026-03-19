package ltw.examsystem.dto.request;

import lombok.Data;

@Data
public class AnswerOptionRequest {
    private Long id; // Dùng khi cập nhật, để trống khi thêm mới
    private String content;
    private Boolean isCorrect;
}
