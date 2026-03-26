package ltw.examsystem.dto.student;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SignupRequest {
    private String username;
    private String fullName;
    private String studentId;
    private String email;
    private String password;
}


