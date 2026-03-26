package ltw.examsystem.controller.admin;

import ltw.examsystem.dto.request.CreateUserRequest;
import ltw.examsystem.dto.response.UserResponse;
import ltw.examsystem.entity.ERole;
import ltw.examsystem.entity.Role;
import ltw.examsystem.entity.User;
import ltw.examsystem.repository.RoleRepository;
import ltw.examsystem.repository.UserRepository;
import ltw.examsystem.dto.response.SubmissionHistoryResponse;
import ltw.examsystem.service.SubmissionService;
import ltw.examsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private UserService userService;

    /**
     * ĐÃ SỬA: Gộp GET All và Search vào chung 1 API
     * Nếu có ?keyword=... thì tìm kiếm, nếu không có thì trả về tất cả
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getUsers(@RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(userService.getUsers(keyword));
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request) {
        try {
            return ResponseEntity.ok(userService.createUser(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * ĐÃ SỬA: Đổi từ /update/{id} thành /{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody CreateUserRequest userRequest) {
        try {
            return ResponseEntity.ok(userService.updateUser(id, userRequest));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // Trả về 400 kèm câu thông báo lỗi
        }
    }

    /**
     * ĐÃ SỬA: Đổi từ /delete/{id} thành /{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok("Đã xóa người dùng thành công");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Lấy lịch sử thi CỦA MỘT USER (Hợp lý vì nó là tài nguyên con của User)
     */
    @GetMapping("/{userId}/results")
    public ResponseEntity<List<SubmissionHistoryResponse>> getStudentResults(@PathVariable Long userId) {
        List<SubmissionHistoryResponse> history = submissionService.getHistoryByUserId(userId);
        return ResponseEntity.ok(history);
    }
}