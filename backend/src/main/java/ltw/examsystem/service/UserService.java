package ltw.examsystem.service;

import ltw.examsystem.dto.request.CreateUserRequest;
import ltw.examsystem.dto.response.UserResponse;

import java.util.List;

public interface UserService {

    // Tạo tài khoản người dùng mới (Dùng chung cho cả Admin thêm User và User tự Đăng ký)
    UserResponse createUser(CreateUserRequest request);

    // Mình viết sẵn luôn hàm Update để bạn tiện chuyển logic từ AdminUserController xuống nhé
    UserResponse updateUser(Long id, CreateUserRequest request);

    List<UserResponse> getUsers(String keyword);

    void deleteUser(Long id);
}