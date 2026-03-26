package ltw.examsystem.dto.student;

import lombok.Data;

@Data
public class TimeLeftResponse {
    private long secondsLeft;
    private boolean isExpired;
}


