package ltw.examsystem.controller.admin;

import ltw.examsystem.dto.response.DashboardStatsResponse;
import ltw.examsystem.dto.response.ExamStatsResponse;
import ltw.examsystem.entity.Submission;
import ltw.examsystem.repository.SubmissionRepository;
import ltw.examsystem.service.ExcelService;
import ltw.examsystem.service.PDFReportService;
import ltw.examsystem.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/admin/stats")
public class AdminStatsController {

    @Autowired
    private StatsService statsService;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private ExcelService excelService;

    @Autowired
    private PDFReportService pdfReportService;

    @GetMapping("/summary")
    public ResponseEntity<DashboardStatsResponse> getSummary() {
        // Giao việc cho Service
        return ResponseEntity.ok(statsService.getSummary());
    }

    @GetMapping("/exam-stats")
    public ResponseEntity<ExamStatsResponse> getExamStats(
            @RequestParam(required = false) Long examId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        // Giao việc cho Service
        return ResponseEntity.ok(statsService.getExamStats(examId, startDate, endDate));
    }

    // --- CÁC HÀM XUẤT FILE ĐÃ ĐƯỢC LÀM GỌN ---

    @GetMapping("/export-excel")
    public ResponseEntity<byte[]> exportToExcel(
            @RequestParam(required = false) Long examId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Double minScore,
            @RequestParam(required = false) Double maxScore) throws IOException {

        // Controller chỉ truyền params thẳng xuống Service
        byte[] excelContent = excelService.exportSubmissionsToExcel(examId, startDate, endDate, minScore, maxScore);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bao_cao_ket_qua.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelContent);
    }

    @GetMapping("/export-pdf")
    public ResponseEntity<byte[]> exportToPdf(
            @RequestParam(required = false) Long examId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Double minScore,
            @RequestParam(required = false) Double maxScore) throws IOException {

        // Controller chỉ truyền params thẳng xuống Service
        byte[] pdfBytes = pdfReportService.exportStatsToPdf(examId, startDate, endDate, minScore, maxScore);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bao_cao_ket_qua.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}