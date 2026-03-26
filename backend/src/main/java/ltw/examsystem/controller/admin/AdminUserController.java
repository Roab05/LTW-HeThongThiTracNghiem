package ltw.examsystem.controller.admin;

import ltw.examsystem.dto.admin.CreateUserRequest;
import ltw.examsystem.dto.student.UserResponse;
import ltw.examsystem.entity.ERole;
import ltw.examsystem.entity.Role;
import ltw.examsystem.entity.User;
import ltw.examsystem.repository.RoleRepository;
import ltw.examsystem.repository.UserRepository;
import ltw.examsystem.dto.admin.SubmissionHistoryResponse;
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
     * Ã„ÂÃƒÆ’ SÃ¡Â»Â¬A: GÃ¡Â»â„¢p GET All vÃƒÂ  Search vÃƒÂ o chung 1 API
     * NÃ¡ÂºÂ¿u cÃƒÂ³ ?keyword=... thÃƒÂ¬ tÃƒÂ¬m kiÃ¡ÂºÂ¿m, nÃ¡ÂºÂ¿u khÃƒÂ´ng cÃƒÂ³ thÃƒÂ¬ trÃ¡ÂºÂ£ vÃ¡Â»Â tÃ¡ÂºÂ¥t cÃ¡ÂºÂ£
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getUsers(@RequestParam(required = false) String keyword) {
        List<User> users;
        if (keyword != null && !keyword.trim().isEmpty()) {
            users = userRepository.findByUsernameContainingIgnoreCaseOrStudentIdContainingIgnoreCaseOrFullNameContainingIgnoreCase(keyword, keyword, keyword);
        } else {
            users = userRepository.findAll();
        }
        return ResponseEntity.ok(convertToDtoList(users));
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body("LÃ¡Â»â€”i: TÃƒÂªn Ã„â€˜Ã„Æ’ng nhÃ¡ÂºÂ­p Ã„â€˜ÃƒÂ£ tÃ¡Â»â€œn tÃ¡ÂºÂ¡i!");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("LÃ¡Â»â€”i: Email Ã„â€˜ÃƒÂ£ Ã„â€˜Ã†Â°Ã¡Â»Â£c sÃ¡Â»Â­ dÃ¡Â»Â¥ng!");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getStudentId() != null && !request.getStudentId().isEmpty()) {
            user.setStudentId(request.getStudentId());
        }

        Role userRole;
        String roleStr = request.getRole();

        if (roleStr != null && roleStr.equalsIgnoreCase("ADMIN")) {
            userRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("LÃ¡Â»â€”i: KhÃƒÂ´ng tÃƒÂ¬m thÃ¡ÂºÂ¥y quyÃ¡Â»Ân ADMIN trong DB."));
        } else {
            userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("LÃ¡Â»â€”i: KhÃƒÂ´ng tÃƒÂ¬m thÃ¡ÂºÂ¥y quyÃ¡Â»Ân USER trong DB."));
        }

        Set<Role> roles = new java.util.HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(convertToDto(savedUser));
    }

    /**
     * Ã„ÂÃƒÆ’ SÃ¡Â»Â¬A: Ã„ÂÃ¡Â»â€¢i tÃ¡Â»Â« /update/{id} thÃƒÂ nh /{id}
     */
    @PutMapping("/{id}")
    @Transactional // Ã„ÂÃ¡ÂºÂ£m bÃ¡ÂºÂ£o tÃƒÂ­nh toÃƒÂ n vÃ¡ÂºÂ¹n dÃ¡Â»Â¯ liÃ¡Â»â€¡u
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody CreateUserRequest userRequest) {
        return userRepository.findById(id).map(user -> {

            if (!user.getUsername().equals(userRequest.getUsername()) &&
                    userRepository.existsByUsername(userRequest.getUsername())) {
                return ResponseEntity.badRequest().body("Lỗi: Tên đăng nhập đã tồn tại!");
            }

            if (!user.getEmail().equals(userRequest.getEmail()) &&
                    userRepository.existsByEmail(userRequest.getEmail())) {
                return ResponseEntity.badRequest().body("Lỗi: Email đã được sử dụng!");
            }

            user.setUsername(userRequest.getUsername());
            user.setEmail(userRequest.getEmail());
            if (userRequest.getFullName() != null && !userRequest.getFullName().isEmpty()) {
                user.setFullName(userRequest.getFullName());
            }

            if (userRequest.getStudentId() != null) {
                user.setStudentId(userRequest.getStudentId());
            }

            if (userRequest.getPassword() != null && !userRequest.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
            }

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
            return ResponseEntity.status(404).body("Lỗi: Không tìm thấy người dùng với ID " + id);
        });
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        return userRepository.findById(id).map(user -> {
            userRepository.delete(user);
            return ResponseEntity.ok("Đã xóa người dùng thành công");
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{userId}/results")
    public ResponseEntity<List<SubmissionHistoryResponse>> getStudentResults(@PathVariable Long userId) {
        List<SubmissionHistoryResponse> history = submissionService.getHistoryByUserId(userId);
        return ResponseEntity.ok(history);
    }

    private UserResponse convertToDto(User user) {
        UserResponse dto = new UserResponse();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setStudentId(user.getStudentId());
        dto.setPassword(user.getPassword());
        return dto;
    }

    private List<UserResponse> convertToDtoList(List<User> users) {
        return users.stream().map(this::convertToDto).collect(Collectors.toList());
    }
}

