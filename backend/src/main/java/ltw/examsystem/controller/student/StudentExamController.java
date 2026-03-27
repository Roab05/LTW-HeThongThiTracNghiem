package ltw.examsystem.controller.student;

import ltw.examsystem.dto.response.ExamDetailResponse;
import ltw.examsystem.dto.response.ExamSummaryResponse;
import ltw.examsystem.entity.ExamStatus;
import ltw.examsystem.entity.ExamType;
import ltw.examsystem.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/student/exams")
public class StudentExamController {

    @Autowired
    private ExamService examService;

    @GetMapping
    public ResponseEntity<List<ExamSummaryResponse>> getExams(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) ExamStatus status,
            @RequestParam(required = false) ExamType type) {
        return ResponseEntity.ok(examService.getExamsForStudent(title, status, type));
    }

    @GetMapping("/{examId}")
    public ResponseEntity<ExamDetailResponse> getExamDetails(@PathVariable Long examId) {
        return ResponseEntity.ok(examService.getExamDetailsForStudent(examId));
    }
}