package ltw.examsystem.dto.request;

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
    // Trường này sẽ nhận giá trị "ADMIN" hoặc "USER" từ Frontend
    private String role;
}