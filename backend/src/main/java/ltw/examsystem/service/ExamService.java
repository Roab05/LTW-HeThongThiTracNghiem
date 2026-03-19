package ltw.examsystem.service;

import ltw.examsystem.dto.response.ExamDetailResponse;

public interface ExamService {
    // Hàm lấy đề thi cho sinh viên (Đã giấu đáp án đúng)
    ExamDetailResponse getExamDetailsForStudent(Long examId);
}