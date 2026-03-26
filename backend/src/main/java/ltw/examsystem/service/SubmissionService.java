package ltw.examsystem.service;

import ltw.examsystem.dto.student.StartExamRequest;
import ltw.examsystem.dto.student.SubmitExamRequest;
import ltw.examsystem.dto.student.SubmissionDetailResponse;
import ltw.examsystem.dto.admin.SubmissionHistoryResponse;
import ltw.examsystem.dto.student.SubmissionResultResponse;
import ltw.examsystem.dto.student.TimeLeftResponse;

import java.util.List;

public interface SubmissionService {
    SubmissionResultResponse submitExam(Long userId, Long submissionId, SubmitExamRequest request);
    Long startExam(Long userId, StartExamRequest request);
    TimeLeftResponse getTimeLeft(Long userId, Long submissionId);

    List<SubmissionHistoryResponse> getHistoryByUserId(Long userId);
    SubmissionDetailResponse getSubmissionDetail(Long submissionId);
    SubmissionDetailResponse getSubmissionDetail(Long userId, Long submissionId);
}
