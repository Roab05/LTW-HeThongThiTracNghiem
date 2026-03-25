package ltw.examsystem.controller.admin;

import ltw.examsystem.dto.response.SubmissionDetailResponse;
import ltw.examsystem.service.PDFReportService;
import ltw.examsystem.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/admin/submissions")
public class AdminSubmissionController {

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private PDFReportService pdfReportService;

    /**
     * Requirement e: Xem chi tiết bài làm cụ thể của sinh viên
     */
    @GetMapping("/{submissionId}")
    public ResponseEntity<SubmissionDetailResponse> getDetailedSubmission(@PathVariable Long submissionId) {
        return ResponseEntity.ok(submissionService.getSubmissionDetail(submissionId));
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