package ltw.examsystem.controller.admin;

import ltw.examsystem.dto.response.SubmissionDetailResponse;
import ltw.examsystem.entity.User;
import ltw.examsystem.repository.UserRepository;
import ltw.examsystem.dto.response.SubmissionHistoryResponse;
import ltw.examsystem.service.PDFReportService;
import ltw.examsystem.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.io.IOException;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PDFReportService pdfReportService;

    /**
     * Requirement b: Lấy danh sách tất cả người dùng (sinh viên)
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    /**
     * Requirement e: Tìm kiếm sinh viên theo tên hoặc email
     */
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String keyword) {
        // Cập nhật để tìm cả theo Username và StudentId
        return ResponseEntity.ok(userRepository.findByUsernameContainingIgnoreCaseOrStudentIdContainingIgnoreCase(keyword, keyword));
    }

    /**
     * Requirement b: Thêm mới một tài khoản sinh viên
     */
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body("Tên đăng nhập đã tồn tại!");
        }
        // Mã hóa mật khẩu trước khi lưu
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return ResponseEntity.ok(userRepository.save(user));
    }

    /**
     * Requirement b: Chỉnh sửa thông tin sinh viên
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        return userRepository.findById(id).map(user -> {
            user.setUsername(userDetails.getUsername());
            user.setEmail(userDetails.getEmail());
            // Chỉ cập nhật mật khẩu nếu Admin nhập mật khẩu mới
            if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            }
            return ResponseEntity.ok(userRepository.save(user));
        }).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Requirement b: Xóa tài khoản sinh viên
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        return userRepository.findById(id).map(user -> {
            userRepository.delete(user);
            return ResponseEntity.ok("Đã xóa người dùng thành công");
        }).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Requirement e: Xem danh sách các kỳ thi mà một sinh viên cụ thể đã tham gia
     */
    @GetMapping("/{userId}/results")
    public ResponseEntity<List<SubmissionHistoryResponse>> getStudentResults(@PathVariable Long userId) {
        // Tái sử dụng logic lấy lịch sử đã viết ở phần Student
        List<SubmissionHistoryResponse> history = submissionService.getHistoryByUserId(userId);
        return ResponseEntity.ok(history);
    }

    /**
     * Requirement e: Xem chi tiết bài làm cụ thể của sinh viên
     */
    @GetMapping("/submissions/{submissionId}")
    public ResponseEntity<SubmissionDetailResponse> getDetailedSubmission(@PathVariable Long submissionId) {
        // Admin có quyền xem bất kỳ submissionId nào
        return ResponseEntity.ok(submissionService.getSubmissionDetail(submissionId));
    }

    /**
     * Requirement e: Xuất phiếu điểm cá nhân dưới dạng PDF để in ấn
     */
    @GetMapping("/submissions/{submissionId}/export-pdf")
    public ResponseEntity<byte[]> exportIndividualPdf(@PathVariable Long submissionId) throws IOException {
        byte[] pdfBytes = pdfReportService.exportIndividualResultToPdf(submissionId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=phieu_diem_sinh_vien.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}