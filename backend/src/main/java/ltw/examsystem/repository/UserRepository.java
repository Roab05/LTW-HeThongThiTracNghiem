package ltw.examsystem.repository;

import ltw.examsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Tìm user theo username để đăng nhập
    Optional<User> findByUsername(String username);

    // Kiểm tra xem username hoặc email đã tồn tại chưa khi đăng ký
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);

    // (Tùy chọn) Tìm user theo mã sinh viên
    Optional<User> findByStudentId(String studentId);
}