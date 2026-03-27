package ltw.examsystem.controller.admin;

import ltw.examsystem.dto.request.CreateUserRequest;
import ltw.examsystem.dto.response.UserResponse;
import ltw.examsystem.dto.response.SubmissionHistoryResponse;
import ltw.examsystem.service.SubmissionService;
import ltw.examsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody CreateUserRequest userRequest) {
        return ResponseEntity.ok(userService.updateUser(id, userRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("Đã xóa người dùng thành công");
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