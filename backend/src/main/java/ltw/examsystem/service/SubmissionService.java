package ltw.examsystem.service;

import ltw.examsystem.dto.request.StartExamRequest;
import ltw.examsystem.dto.request.SubmitExamRequest;
import ltw.examsystem.dto.response.SubmissionDetailResponse;
import ltw.examsystem.dto.response.SubmissionHistoryResponse;
import ltw.examsystem.dto.response.SubmissionResultResponse;
import ltw.examsystem.dto.response.TimeLeftResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface SubmissionService {
    // Hàm xử lý nộp bài và chấm điểm tự động
    SubmissionResultResponse submitExam(Long userId, Long submissionId, SubmitExamRequest request);
    Long startExam(Long userId, StartExamRequest request);
    TimeLeftResponse getTimeLeft(Long userId, Long submissionId);
    List<SubmissionHistoryResponse> filterSubmissions(Long examId, LocalDateTime startDate, LocalDateTime endDate, Double minScore, Double maxScore);


    // 2 Hàm mới thêm vào
    List<SubmissionHistoryResponse> getHistoryByUserId(Long userId);
    SubmissionDetailResponse getSubmissionDetail(Long submissionId);
    SubmissionDetailResponse getSubmissionDetail(Long userId, Long submissionId);
}