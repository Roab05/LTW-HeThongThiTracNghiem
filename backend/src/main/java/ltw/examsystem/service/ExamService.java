package ltw.examsystem.service;

import ltw.examsystem.dto.request.ExamRequest;
import ltw.examsystem.dto.response.ExamDetailResponse;
import ltw.examsystem.dto.response.ExamSummaryResponse;
import ltw.examsystem.entity.ExamStatus;
import ltw.examsystem.entity.ExamType;

import java.util.List;

public interface ExamService {
    // Hàm lấy đề thi cho sinh viên (Đã giấu đáp án đúng)
    ExamDetailResponse getExamDetailsForStudent(Long examId);

    ExamDetailResponse getExamDetailsForAdmin(Long examId);

    ExamSummaryResponse createExam(ExamRequest request);

    ExamSummaryResponse updateExam(Long id, ExamRequest request);

    ExamSummaryResponse togglePublish(Long id, boolean publish);

    List<ExamSummaryResponse> getAllExams(String title, ExamStatus status, ExamType type);

    List<ExamSummaryResponse> getExamsForStudent(String title, ExamStatus status, ExamType type);

    void deleteExam(Long id);
}