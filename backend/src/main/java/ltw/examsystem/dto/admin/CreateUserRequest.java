package ltw.examsystem.dto.admin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequest {
    private String username;
    private String email;
    private String password;
    private String studentId;
    private String fullName;

    private String role;
}


