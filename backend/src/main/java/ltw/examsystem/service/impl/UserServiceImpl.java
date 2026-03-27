package ltw.examsystem.service.impl;

import ltw.examsystem.dto.request.CreateUserRequest;
import ltw.examsystem.dto.response.UserResponse;
import ltw.examsystem.entity.ERole;
import ltw.examsystem.entity.Role;
import ltw.examsystem.entity.User;
import ltw.examsystem.repository.RoleRepository;
import ltw.examsystem.repository.UserRepository;
import ltw.examsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Lỗi: Tên đăng nhập đã tồn tại!");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Lỗi: Email đã được sử dụng!");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getStudentId() != null && !request.getStudentId().isEmpty()) {
            user.setStudentId(request.getStudentId());
        }

        String roleStr = request.getRole();
        Role userRole = roleRepository.findByName(
                (roleStr != null && roleStr.equalsIgnoreCase("ADMIN")) ? ERole.ROLE_ADMIN : ERole.ROLE_USER
        ).orElseThrow(() -> new RuntimeException("Không tìm thấy quyền trong DB."));

        Set<Role> roles = new java.util.HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        return convertToDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, CreateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lỗi: Không tìm thấy người dùng với ID " + id));

        // 1. Kiểm tra trùng Username (trừ chính nó)
        if (!user.getUsername().equals(request.getUsername()) &&
                userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Lỗi: Tên đăng nhập đã tồn tại!");
        }

        // 2. Kiểm tra trùng Email (trừ chính nó)
        if (!user.getEmail().equals(request.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Lỗi: Email đã được sử dụng!");
        }

        // 3. Cập nhật thông tin cơ bản
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        if (request.getFullName() != null && !request.getFullName().isEmpty()) {
            user.setFullName(request.getFullName());
        }
        if (request.getStudentId() != null) {
            user.setStudentId(request.getStudentId());
        }
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // 4. Cập nhật Role
        if (request.getRole() != null) {
            String roleStr = request.getRole();
            Role userRole = roleRepository.findByName(
                    roleStr.equalsIgnoreCase("ADMIN") ? ERole.ROLE_ADMIN : ERole.ROLE_USER
            ).orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy quyền."));

            Set<Role> roles = new java.util.HashSet<>();
            roles.add(userRole);
            user.setRoles(roles);
        }

        return convertToDto(userRepository.save(user));
    }

    @Override
    public List<UserResponse> getUsers(String keyword) {
        List<User> users;
        if (keyword != null && !keyword.trim().isEmpty()) {
            users = userRepository.findByUsernameContainingIgnoreCaseOrStudentIdContainingIgnoreCaseOrFullNameContainingIgnoreCase(keyword, keyword, keyword);
        } else {
            users = userRepository.findAll();
        }
        return users.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng"));
        userRepository.delete(user);
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
}