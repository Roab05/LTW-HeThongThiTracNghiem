package ltw.examsystem.controller;

import ltw.examsystem.dto.request.LoginRequest;
import ltw.examsystem.dto.request.SignupRequest;
import ltw.examsystem.entity.User;
import ltw.examsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signupRequest) {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Lỗi: Tên đăng nhập đã tồn tại!");
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Lỗi: Email đã được sử dụng!");
        }

        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());

        // MÃ HÓA MẬT KHẨU TRƯỚC KHI LƯU
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));

        userRepository.save(user);
        return ResponseEntity.ok("Đăng ký thành công!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // KHÔNG DÙNG .equals() nữa, mà dùng .matches() của PasswordEncoder
            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                return ResponseEntity.ok(user);
            }
        }

        return ResponseEntity.status(401).body("Lỗi: Sai tên đăng nhập hoặc mật khẩu!");
    }
}