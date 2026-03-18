package ltw.examsystem.repository;

import ltw.examsystem.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    // Lấy toàn bộ câu hỏi của một kỳ thi cụ thể
    List<Question> findByExamId(Long examId);
}