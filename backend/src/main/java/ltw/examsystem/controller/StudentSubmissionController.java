package ltw.examsystem.controller;

import ltw.examsystem.dto.request.StartExamRequest;
import ltw.examsystem.dto.request.SubmitExamRequest;
import ltw.examsystem.dto.response.SubmissionDetailResponse;
import ltw.examsystem.dto.response.SubmissionHistoryResponse;
import ltw.examsystem.dto.response.SubmissionResultResponse;
import ltw.examsystem.dto.response.TimeLeftResponse;
import ltw.examsystem.security.SecurityUtils;
import ltw.examsystem.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/student/submissions")
public class StudentSubmissionController {

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private SecurityUtils securityUtils;

    @PostMapping
    public ResponseEntity<Long> startExam(@RequestBody StartExamRequest request) {
        Long currentUserId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(submissionService.startExam(currentUserId, request));
    }

    /**
     * ĐÃ SỬA: Thêm {submissionId} vào URL để biết chính xác đang nộp cho bài nào
     */
    @PostMapping("/{submissionId}/submit")
    public ResponseEntity<SubmissionResultResponse> submitExam(
            @PathVariable Long submissionId,
            @RequestBody SubmitExamRequest request) {

        // 1. Lấy ID của người đang request
        Long currentUserId = securityUtils.getCurrentUserId();

        // 2. Truyền cả currentUserId và submissionId xuống Service
        SubmissionResultResponse result = submissionService.submitExam(currentUserId, submissionId, request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/my-history")
    public ResponseEntity<List<SubmissionHistoryResponse>> getSubmissionHistory() {
        Long currentUserId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(submissionService.getHistoryByUserId(currentUserId));
    }

    @GetMapping("/{submissionId}")
    public ResponseEntity<SubmissionDetailResponse> getSubmissionDetail(@PathVariable Long submissionId) {
        Long currentUserId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(submissionService.getSubmissionDetail(currentUserId, submissionId));
    }

    @GetMapping("/{submissionId}/time-left")
    public ResponseEntity<TimeLeftResponse> getTimeLeft(@PathVariable Long submissionId) {
        Long currentUserId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(submissionService.getTimeLeft(currentUserId, submissionId));
    }
}