package ltw.examsystem.config;

import ltw.examsystem.entity.ERole;
import ltw.examsystem.entity.Role;
import ltw.examsystem.entity.User;
import ltw.examsystem.repository.RoleRepository;
import ltw.examsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Kiểm tra xem đã có admin_code chưa, nếu chưa thì hệ thống tự tạo
        if (!userRepository.existsByUsername("admin")) {
            User user = new User();
            user.setUsername("admin");
            user.setEmail("admin@code.com");
            user.setFullName("admin");
            // ĐỂ SPRING BOOT TỰ MÃ HÓA BẰNG THUẬT TOÁN CHUẨN CỦA NÓ
            user.setPassword(passwordEncoder.encode("123456"));

            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy quyền ADMIN"));

            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            user.setRoles(roles);

            userRepository.save(user);
            System.out.println("==========================================================");
            System.out.println("✅ ĐÃ TẠO THÀNH CÔNG TÀI KHOẢN ADMIN: admin_code / 123456 ");
            System.out.println("==========================================================");
        }
    }
}