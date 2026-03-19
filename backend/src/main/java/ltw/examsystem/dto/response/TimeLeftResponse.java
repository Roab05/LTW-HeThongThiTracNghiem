package ltw.examsystem.dto.response;

import lombok.Data;

@Data
public class TimeLeftResponse {
    private long secondsLeft;
    private boolean isExpired;
}