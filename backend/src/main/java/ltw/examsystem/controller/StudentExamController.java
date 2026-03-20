package ltw.examsystem.controller;

import ltw.examsystem.dto.response.ExamDetailResponse;
import ltw.examsystem.dto.response.ExamSummaryResponse;
import ltw.examsystem.entity.Exam;
import ltw.examsystem.entity.ExamStatus;
import ltw.examsystem.entity.ExamType;
import ltw.examsystem.repository.ExamRepository;
import ltw.examsystem.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/student/exams")
public class StudentExamController {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private ExamService examService;

    /**
     * API 1: Lấy danh sách tất cả các kỳ thi hiển thị ra trang chủ
     */
    @GetMapping
    public ResponseEntity<List<ExamSummaryResponse>> getExams(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) ExamStatus status,
            @RequestParam(required = false) ExamType type) {

        // 1. Lấy dữ liệu từ Repo (có lọc)
        List<Exam> exams = examRepository.findByFilters(title, status, type);

        // 2. Chuyển đổi sang DTO rút gọn
        List<ExamSummaryResponse> summaryList = exams.stream().map(exam -> {
            ExamSummaryResponse dto = new ExamSummaryResponse();
            dto.setId(exam.getId());
            dto.setTitle(exam.getTitle());
            dto.setDescription(exam.getDescription());
            dto.setType(exam.getType());
            dto.setStatus(exam.getStatus());
            dto.setDurationMinutes(exam.getDurationMinutes());
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(summaryList);
    }

    @GetMapping("/{examId}")
    public ResponseEntity<ExamDetailResponse> getExamDetails(@PathVariable Long examId) {
        ExamDetailResponse response = examService.getExamDetailsForStudent(examId);
        return ResponseEntity.ok(response);
    }
}