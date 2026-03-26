package ltw.examsystem.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserResponse {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String studentId;
    private String password;
}