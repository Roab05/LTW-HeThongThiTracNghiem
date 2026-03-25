package ltw.examsystem.controller.admin;

import ltw.examsystem.dto.request.ExamRequest;
import ltw.examsystem.dto.request.QuestionRequest;
import ltw.examsystem.dto.response.AnswerOptionResponse;
import ltw.examsystem.dto.response.ExamSummaryResponse;
import ltw.examsystem.dto.response.QuestionResponse;
import ltw.examsystem.entity.AnswerOption;
import ltw.examsystem.entity.Exam;
import ltw.examsystem.entity.Question;
import ltw.examsystem.repository.ExamRepository;
import ltw.examsystem.repository.QuestionRepository;
import ltw.examsystem.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/admin/exams")
public class AdminExamController {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ExcelService excelService;

    /**
     * Requirement c: Lấy danh sách kỳ thi để Admin quản lý
     */
    @GetMapping
    public ResponseEntity<List<ExamSummaryResponse>> getAllExams() {
        List<Exam> exams = examRepository.findAll();
        return ResponseEntity.ok(exams.stream().map(this::convertToSummaryDto).collect(Collectors.toList()));
    }

    /**
     * Requirement c: Tạo kỳ thi mới (thông tin cơ bản)
     */
    @PostMapping
    public ResponseEntity<ExamSummaryResponse> createExam(@RequestBody ExamRequest request) {
        Exam exam = new Exam();
        mapRequestToEntity(request, exam);
        Exam saved = examRepository.save(exam);
        return ResponseEntity.ok(convertToSummaryDto(saved));
    }

    /**
     * Requirement c: Chỉnh sửa thông tin kỳ thi
     */
    @PutMapping("/{id}")
    public ResponseEntity<ExamSummaryResponse> updateExam(@PathVariable Long id, @RequestBody ExamRequest request) {
        return examRepository.findById(id).map(exam -> {
            mapRequestToEntity(request, exam);
            return ResponseEntity.ok(convertToSummaryDto(examRepository.save(exam)));
        }).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Requirement c: Xóa kỳ thi
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExam(@PathVariable Long id) {
        examRepository.deleteById(id);
        return ResponseEntity.ok("Đã xóa kỳ thi thành công");
    }

    @PostMapping("/{examId}/questions")
    @Transactional
    public ResponseEntity<?> addQuestion(@PathVariable Long examId, @RequestBody QuestionRequest request) {
        return examRepository.findById(examId).map(exam -> {
            Question question = new Question();
            question.setContent(request.getContent());
            question.setExplanation(request.getExplanation());
            question.setExam(exam);

            List<AnswerOption> options = request.getOptions().stream().map(optReq -> {
                AnswerOption opt = new AnswerOption();
                opt.setContent(optReq.getContent());
                opt.setIsCorrect(optReq.getIsCorrect());
                opt.setQuestion(question);
                return opt;
            }).collect(Collectors.toList());
            question.setOptions(options);

            Question savedQuestion = questionRepository.save(question);

            Map<String, Object> response = new HashMap<>();
            response.put("id", savedQuestion.getId());
            response.put("content", savedQuestion.getContent());
            response.put("message", "Thêm câu hỏi thành công!");

            return ResponseEntity.ok(response);
        }).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Requirement c: Chỉnh sửa nội dung câu hỏi và các lựa chọn
     */
    @PutMapping("/questions/{questionId}")
    @Transactional
    public ResponseEntity<QuestionResponse> updateQuestion(@PathVariable Long questionId, @RequestBody QuestionRequest request) {
        return questionRepository.findById(questionId).map(question -> {
            question.setContent(request.getContent());
            question.setExplanation(request.getExplanation());
            question.getOptions().clear();

            request.getOptions().forEach(optReq -> {
                AnswerOption opt = new AnswerOption();
                opt.setContent(optReq.getContent());
                opt.setIsCorrect(optReq.getIsCorrect());
                opt.setQuestion(question);
                question.getOptions().add(opt);
            });

            Question saved = questionRepository.save(question);
            return ResponseEntity.ok(convertToQuestionDto(saved));
        }).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Requirement c: Xóa một câu hỏi (Tự động xóa các lựa chọn kèm theo)
     */
    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long questionId) {
        if (!questionRepository.existsById(questionId)) {
            return ResponseEntity.notFound().build();
        }
        questionRepository.deleteById(questionId);
        return ResponseEntity.ok("Đã xóa câu hỏi thành công");
    }

    /**
     * Requirement c: NHẬP ĐỀ THI TỪ EXCEL
     * Nhận 1 file .xlsx và đẩy vào kỳ thi có ID tương ứng
     */
    @PostMapping("/{examId}/import-questions")
    public ResponseEntity<?> importQuestions(@PathVariable Long examId, @RequestParam("file") MultipartFile file) {
        try {
            excelService.importQuestionsFromExcel(examId, file);
            return ResponseEntity.ok("Đã nhập danh sách câu hỏi thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi nhập file: " + e.getMessage());
        }
    }

    private ExamSummaryResponse convertToSummaryDto(Exam exam) {
        ExamSummaryResponse dto = new ExamSummaryResponse();
        dto.setId(exam.getId());
        dto.setTitle(exam.getTitle());
        dto.setDescription(exam.getDescription());
        dto.setType(exam.getType());
        dto.setStatus(exam.getStatus());
        dto.setDurationMinutes(exam.getDurationMinutes());
        dto.setIsPublished(exam.getIsPublished()); // Đừng quên field này nhé
        return dto;
    }

    private QuestionResponse convertToQuestionDto(Question q) {
        QuestionResponse dto = new QuestionResponse();
        dto.setId(q.getId());
        dto.setContent(q.getContent());

        // Cần map thêm list options để không bị null
        if (q.getOptions() != null) {
            dto.setOptions(q.getOptions().stream().map(opt -> {
                AnswerOptionResponse optDto = new AnswerOptionResponse();
                optDto.setId(opt.getId());
                optDto.setContent(opt.getContent());
                optDto.setIsCorrect(opt.getIsCorrect());
                return optDto;
            }).collect(Collectors.toList()));
        }
        return dto;
    }

    private void mapRequestToEntity(ExamRequest request, Exam exam) {
        exam.setTitle(request.getTitle());
        exam.setDescription(request.getDescription());
        exam.setDurationMinutes(request.getDurationMinutes());
        exam.setStatus(request.getStatus());
        exam.setType(request.getType());
        if (request.getIsPublished() != null) exam.setIsPublished(request.getIsPublished());
    }
}