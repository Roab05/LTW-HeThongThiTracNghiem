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
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * ĐÃ SỬA: Gộp GET All và Search vào chung 1 API
     * Nếu có ?keyword=... thì tìm kiếm, nếu không có thì trả về tất cả
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getUsers(@RequestParam(required = false) String keyword) {
        List<User> users;
        if (keyword != null && !keyword.trim().isEmpty()) {
            users = userRepository.findByUsernameContainingIgnoreCaseOrStudentIdContainingIgnoreCase(keyword, keyword);
        } else {
            users = userRepository.findAll();
        }
        return ResponseEntity.ok(convertToDtoList(users));
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request) {
        // 1. Kiểm tra trùng lặp
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body("Lỗi: Tên đăng nhập đã tồn tại!");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Lỗi: Email đã được sử dụng!");
        }

        // 2. Chuyển dữ liệu từ Request sang Entity
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        if (request.getStudentId() != null && !request.getStudentId().isEmpty()) {
            user.setStudentId(request.getStudentId());
        }

        // 3. Xử lý phân quyền linh hoạt dựa trên tham số 'role' truyền lên
        Role userRole;
        String roleStr = request.getRole();

        // Nếu Frontend truyền lên "ADMIN", gán quyền quản trị trị viên
        if (roleStr != null && roleStr.equalsIgnoreCase("ADMIN")) {
            userRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy quyền ADMIN trong DB."));
        } else {
            // Mặc định nếu không truyền hoặc truyền sai thì gán quyền sinh viên (USER)
            userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy quyền USER trong DB."));
        }

        Set<Role> roles = new java.util.HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        // 4. Lưu và trả về kết quả
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(convertToDto(savedUser));
    }

    /**
     * ĐÃ SỬA: Đổi từ /update/{id} thành /{id}
     */
    @PutMapping("/{id}")
    @Transactional // Đảm bảo tính toàn vẹn dữ liệu
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody CreateUserRequest userRequest) {
        return userRepository.findById(id).map(user -> {

            // 1. Kiểm tra trùng Username (trừ chính nó)
            if (!user.getUsername().equals(userRequest.getUsername()) &&
                    userRepository.existsByUsername(userRequest.getUsername())) {
                return ResponseEntity.badRequest().body("Lỗi: Tên đăng nhập đã tồn tại!");
            }

            // 2. Kiểm tra trùng Email (trừ chính nó)
            if (!user.getEmail().equals(userRequest.getEmail()) &&
                    userRepository.existsByEmail(userRequest.getEmail())) {
                return ResponseEntity.badRequest().body("Lỗi: Email đã được sử dụng!");
            }

            // 3. Cập nhật các thông tin cơ bản
            user.setUsername(userRequest.getUsername());
            user.setEmail(userRequest.getEmail());

            if (userRequest.getStudentId() != null) {
                user.setStudentId(userRequest.getStudentId());
            }

            if (userRequest.getPassword() != null && !userRequest.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
            }

            // 4. BỔ SUNG: Cập nhật Role nếu có truyền lên
            if (userRequest.getRole() != null) {
                String roleStr = userRequest.getRole();
                Role userRole = roleRepository.findByName(
                        roleStr.equalsIgnoreCase("ADMIN") ? ERole.ROLE_ADMIN : ERole.ROLE_USER
                ).orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy quyền."));

                Set<Role> roles = new HashSet<>();
                roles.add(userRole);
                user.setRoles(roles);
            }

            userRepository.save(user);
            return ResponseEntity.ok(convertToDto(user));

        }).orElseGet(() -> {
            // Trả về lỗi rõ ràng để tránh Spring Security redirect sang /error gây lỗi 401
            return ResponseEntity.status(404).body("Lỗi: Không tìm thấy người dùng với ID " + id);
        });
    }

    /**
     * ĐÃ SỬA: Đổi từ /delete/{id} thành /{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        return userRepository.findById(id).map(user -> {
            userRepository.delete(user);
            return ResponseEntity.ok("Đã xóa người dùng thành công");
        }).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Lấy lịch sử thi CỦA MỘT USER (Hợp lý vì nó là tài nguyên con của User)
     */
    @GetMapping("/{userId}/results")
    public ResponseEntity<List<SubmissionHistoryResponse>> getStudentResults(@PathVariable Long userId) {
        List<SubmissionHistoryResponse> history = submissionService.getHistoryByUserId(userId);
        return ResponseEntity.ok(history);
    }

    // --- Hàm bổ trợ để chuyển đổi Entity sang DTO ---
    private UserResponse convertToDto(User user) {
        UserResponse dto = new UserResponse();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setStudentId(user.getStudentId());
        return dto;
    }

    private List<UserResponse> convertToDtoList(List<User> users) {
        return users.stream().map(this::convertToDto).collect(Collectors.toList());
    }
}