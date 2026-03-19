package ltw.examsystem.repository;

import ltw.examsystem.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    @Query("SELECT AVG(s.score) FROM Submission s")
    Double getGlobalAverageScore();

    List<Submission> findByExamId(Long examId);

    List<Submission> findByUserId(Long userId);

    /**
     * Requirement d: Lọc kết quả nâng cao
     * Sử dụng cấu trúc (column IS NULL OR ...) để xử lý tham số tùy chọn
     */
    @Query("SELECT s FROM Submission s WHERE " +
            "(:examId IS NULL OR s.exam.id = :examId) AND " +
            "(:startDate IS NULL OR s.submitTime >= :startDate) AND " +
            "(:endDate IS NULL OR s.submitTime <= :endDate) AND " +
            "(:minScore IS NULL OR s.score >= :minScore) AND " +
            "(:maxScore IS NULL OR s.score <= :maxScore)")
    List<Submission> filterSubmissions(
            @Param("examId") Long examId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("minScore") Double minScore,
            @Param("maxScore") Double maxScore);
}