package ltw.examsystem.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter @Setter
@JsonInclude(JsonInclude.Include.NON_NULL) // CỰC KỲ QUAN TRỌNG: Chỉ hiện những trường có dữ liệu
public class ExamDetailResponse {
    private Long id;
    private String title;
    private String description;     // Mới bổ sung
    private String type;            // Mới bổ sung
    private String status;          // Mới bổ sung
    private Boolean isPublished;    // Mới bổ sung
    private Integer durationMinutes;

    // Danh sách câu hỏi (Dùng chung cho cả Admin/User nhưng khác kiểu dữ liệu bên trong)
    private List<?> questions;
}