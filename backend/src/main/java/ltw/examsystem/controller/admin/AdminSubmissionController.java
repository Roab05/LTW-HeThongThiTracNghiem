package ltw.examsystem.controller.admin;

import ltw.examsystem.dto.response.SubmissionDetailResponse;
import ltw.examsystem.dto.response.SubmissionHistoryResponse;
import ltw.examsystem.entity.Submission;
import ltw.examsystem.repository.SubmissionRepository;
import ltw.examsystem.service.PDFReportService;
import ltw.examsystem.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/admin/submissions")
public class AdminSubmissionController {

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private PDFReportService pdfReportService;

    @GetMapping("/{submissionId}")
    public ResponseEntity<SubmissionDetailResponse> getDetailedSubmission(@PathVariable Long submissionId) {
        return ResponseEntity.ok(submissionService.getSubmissionDetail(submissionId));
    }

    @GetMapping("")
    public ResponseEntity<List<SubmissionHistoryResponse>> getSubmissions(
            @RequestParam(required = false) Long examId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Double minScore,
            @RequestParam(required = false) Double maxScore) {

        // Gọi hàm lọc linh hoạt từ Repository
        List<Submission> results = submissionRepository.filterSubmissions(
                examId, startDate, endDate, minScore, maxScore);

        // Chuyển đổi sang DTO để hiển thị trên bảng thống kê của Frontend
        List<SubmissionHistoryResponse> response = results.stream().map(s -> {
            SubmissionHistoryResponse dto = new SubmissionHistoryResponse();
            dto.setSubmissionId(s.getId());
            dto.setExamTitle(s.getExam().getTitle());
            dto.setScore(s.getScore());
            dto.setSubmitTime(s.getSubmitTime());
            dto.setCorrectAnswers(s.getCorrectAnswers());
            dto.setTotalQuestions(s.getTotalQuestions());
            dto.setStatus(s.getStatus().toString());
            dto.setFullName(s.getUser().getFullName());
            dto.setStudentId(s.getUser().getStudentId());
            dto.setStudentId(s.getUser().getStudentId());
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * Requirement e: Xuất phiếu điểm cá nhân dưới dạng PDF để in ấn
     */
    @GetMapping("/{submissionId}/export-pdf")
    public ResponseEntity<byte[]> exportIndividualPdf(@PathVariable Long submissionId) throws IOException {
        byte[] pdfBytes = pdfReportService.exportIndividualResultToPdf(submissionId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=phieu_diem_sinh_vien.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}