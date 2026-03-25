package ltw.examsystem.dto.request;

import ltw.examsystem.entity.ExamStatus;
import ltw.examsystem.entity.ExamType;
import lombok.Data;

@Data
public class ExamRequest {
    private String title;
    private String description;
    private Integer durationMinutes;
    private ExamStatus status; // FREE, TIME_RESTRICTED
    private ExamType type;
    private Boolean isPublished;// PRACTICE, MIDTERM, FINAL
}