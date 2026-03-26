package ltw.examsystem.controller;

import ltw.examsystem.dto.request.LoginRequest;
import ltw.examsystem.dto.request.SignupRequest;
import ltw.examsystem.dto.response.JwtResponse;
import ltw.examsystem.security.JwtUtils;
import ltw.examsystem.security.UserDetailsImpl;
import ltw.examsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserService userService; // Tiêm UserService vào đây

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signupRequest) {
        try {
            // Chuyển đổi SignupRequest thành CreateUserRequest để tái sử dụng UserService
            ltw.examsystem.dto.request.CreateUserRequest req = new ltw.examsystem.dto.request.CreateUserRequest();
            req.setUsername(signupRequest.getUsername());
            req.setEmail(signupRequest.getEmail());
            req.setPassword(signupRequest.getPassword());
            req.setFullName(signupRequest.getFullName());
            req.setStudentId(signupRequest.getStudentId());
            req.setRole("USER"); // Đăng ký ngoài luôn là USER

            userService.createUser(req); // Giao toàn bộ việc kiểm tra và lưu cho Service
            return ResponseEntity.ok("Đăng ký thành công!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles));
    }
}