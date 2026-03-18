package ltw.examsystem.repository;

import ltw.examsystem.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    // Xem lịch sử các bài thi mà một sinh viên đã làm
    List<Submission> findByUserId(Long userId);

    // Admin xem danh sách bài nộp của một kỳ thi cụ thể
    List<Submission> findByExamId(Long examId);
}