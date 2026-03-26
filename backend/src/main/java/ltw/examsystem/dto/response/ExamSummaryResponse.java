package ltw.examsystem.dto.response;

import lombok.Getter;
import lombok.Setter;
import ltw.examsystem.entity.ExamStatus;
import ltw.examsystem.entity.ExamType;

import java.time.LocalDateTime;

@Getter @Setter
public class ExamSummaryResponse {
    private Long id;
    private String title;
    private String description;
    private ExamType type;
    private ExamStatus status;
    private Boolean isPublished;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer durationMinutes;
}