package ltw.examsystem.controller;

import ltw.examsystem.dto.request.StartExamRequest;
import ltw.examsystem.dto.request.SubmitExamRequest;
import ltw.examsystem.dto.response.SubmissionDetailResponse;
import ltw.examsystem.dto.response.SubmissionHistoryResponse;
import ltw.examsystem.dto.response.SubmissionResultResponse;
import ltw.examsystem.dto.response.TimeLeftResponse;
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

    @PostMapping
    public ResponseEntity<Long> startExam(@RequestBody StartExamRequest request) {
        // Trả về ID của lần nộp bài mới được tạo
        return ResponseEntity.ok(submissionService.startExam(request));
    }

    /**
     * API 1: Nộp bài thi và nhận điểm ngay lập tức
     */
    @PostMapping("/submit")
    public ResponseEntity<SubmissionResultResponse> submitExam(@RequestBody SubmitExamRequest request) {
        SubmissionResultResponse result = submissionService.submitExam(request);
        return ResponseEntity.ok(result);
    }

    /**
     * API 2: Lấy lịch sử các bài thi
     */
    @GetMapping("/history/{userId}")
    public ResponseEntity<List<SubmissionHistoryResponse>> getSubmissionHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(submissionService.getHistoryByUserId(userId));
    }

    /**
     * API 3: Xem chi tiết một bài đã nộp
     */
    @GetMapping("/{submissionId}")
    public ResponseEntity<SubmissionDetailResponse> getSubmissionDetail(@PathVariable Long submissionId) {
        return ResponseEntity.ok(submissionService.getSubmissionDetail(submissionId));
    }

    @GetMapping("/{submissionId}/time-left")
    public ResponseEntity<TimeLeftResponse> getTimeLeft(@PathVariable Long submissionId) {
        return ResponseEntity.ok(submissionService.getTimeLeft(submissionId));
    }
}