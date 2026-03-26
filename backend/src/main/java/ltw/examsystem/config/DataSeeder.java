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
        if (!userRepository.existsByUsername("admin")) {
            User user = new User();
            user.setUsername("admin");
            user.setEmail("admin@code.com");
            user.setFullName("admin");
            user.setPassword(passwordEncoder.encode("123456"));

            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("KhÃ´ng tÃ¬m tháº¥y quyá»n ADMIN"));

            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            user.setRoles(roles);

            userRepository.save(user);
            System.out.println("==========================================================");
            System.out.println("âœ… ÄÃƒ Táº O THÃ€NH CÃ”NG TÃ€I KHOáº¢N ADMIN: admin_code / 123456 ");
            System.out.println("==========================================================");
        }
        }
    }
