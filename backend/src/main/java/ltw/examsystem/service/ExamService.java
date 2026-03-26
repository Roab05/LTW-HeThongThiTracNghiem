package ltw.examsystem.service;

import ltw.examsystem.dto.admin.ExamDetailResponse;

public interface ExamService {
    ExamDetailResponse getExamDetailsForStudent(Long examId);

    ExamDetailResponse getExamDetailsForAdmin(Long examId);
}
