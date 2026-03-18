package ltw.examsystem.repository;

import ltw.examsystem.entity.Exam;
import ltw.examsystem.entity.ExamStatus;
import ltw.examsystem.entity.ExamType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {

    // Tìm kiếm tổng hợp: Tên, Trạng thái và Loại bài thi
    @Query("SELECT e FROM Exam e WHERE " +
            "(:title IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(:status IS NULL OR e.status = :status) AND " +
            "(:type IS NULL OR e.type = :type)")
    List<Exam> findByFilters(@Param("title") String title,
                             @Param("status") ExamStatus status,
                             @Param("type") ExamType type);
}