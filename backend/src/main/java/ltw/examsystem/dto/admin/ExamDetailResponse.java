package ltw.examsystem.dto.admin;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExamDetailResponse {
    private Long id;
    private String title;
    private String description;
    private String type;
    private String status;
    private Boolean isPublished;
    private LocalDateTime startTime;
    private LocalDateTime endTime;// Má»›i bá»• sung
    private Integer durationMinutes;

    private List<?> questions;
}


