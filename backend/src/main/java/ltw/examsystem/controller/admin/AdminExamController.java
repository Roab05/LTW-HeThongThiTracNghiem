package ltw.examsystem.controller.admin;

import ltw.examsystem.dto.request.ExamRequest;
import ltw.examsystem.dto.request.QuestionRequest;
import ltw.examsystem.dto.response.ExamDetailResponse;
import ltw.examsystem.dto.response.ExamSummaryResponse;
import ltw.examsystem.service.ExamService;
import ltw.examsystem.service.ExcelService;
import ltw.examsystem.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/admin/exams")
public class AdminExamController {

    @Autowired
    private ExamService examService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private ExcelService excelService;

    @GetMapping
    public ResponseEntity<List<ExamSummaryResponse>> getAllExams() {
        return ResponseEntity.ok(examService.getAllExams());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExamDetailResponse> getExamDetail(@PathVariable Long id) {
        return ResponseEntity.ok(examService.getExamDetailsForAdmin(id));
    }

    @PostMapping
    public ResponseEntity<?> createExam(@RequestBody ExamRequest request) {
        try {
            return ResponseEntity.ok(examService.createExam(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateExam(@PathVariable Long id, @RequestBody ExamRequest request) {
        try {
            return ResponseEntity.ok(examService.updateExam(id, request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExam(@PathVariable Long id) {
        try {
            examService.deleteExam(id);
            return ResponseEntity.ok("Đã xóa kỳ thi thành công");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/publish")
    public ResponseEntity<?> togglePublish(@PathVariable Long id, @RequestParam boolean publish) {
        try {
            return ResponseEntity.ok(examService.togglePublish(id, publish));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{examId}/questions")
    public ResponseEntity<?> addQuestion(@PathVariable Long examId, @RequestBody QuestionRequest request) {
        try {
            return ResponseEntity.ok(questionService.addQuestion(examId, request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/questions/{questionId}")
    public ResponseEntity<?> updateQuestion(@PathVariable Long questionId, @RequestBody QuestionRequest request) {
        try {
            return ResponseEntity.ok(questionService.updateQuestion(questionId, request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long questionId) {
        try {
            questionService.deleteQuestion(questionId);
            return ResponseEntity.ok("Đã xóa câu hỏi thành công");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{examId}/import-questions")
    public ResponseEntity<?> importQuestions(@PathVariable Long examId, @RequestParam("file") MultipartFile file) {
        try {
            excelService.importQuestionsFromExcel(examId, file);
            return ResponseEntity.ok("Đã nhập danh sách câu hỏi thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi nhập file: " + e.getMessage());
        }
    }
}