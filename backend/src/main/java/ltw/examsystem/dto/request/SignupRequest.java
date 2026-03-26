package ltw.examsystem.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SignupRequest {
    private String username;
    private String fullName;
    private String studentId;
    private String email;
    private String password;
    // Lưu ý: confirmPassword thường được check ở Frontend bằng JS
    // trước khi gửi sang Backend.
}