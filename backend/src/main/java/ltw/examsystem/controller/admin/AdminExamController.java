package ltw.examsystem.controller.admin;

import ltw.examsystem.dto.request.ExamRequest;
import ltw.examsystem.dto.request.QuestionRequest;
import ltw.examsystem.dto.response.AnswerOptionResponse;
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

import java.util.List;
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
    public ResponseEntity<List<Exam>> getAllExams() {
        return ResponseEntity.ok(examRepository.findAll());
    }

    /**
     * Requirement c: Tạo kỳ thi mới (thông tin cơ bản)
     */
    @PostMapping
    public ResponseEntity<Exam> createExam(@RequestBody ExamRequest request) {
        Exam exam = new Exam();
        exam.setTitle(request.getTitle());
        exam.setDescription(request.getDescription());
        exam.setDurationMinutes(request.getDurationMinutes());
        exam.setStatus(request.getStatus());
        exam.setType(request.getType());
        return ResponseEntity.ok(examRepository.save(exam));
    }

    /**
     * Requirement c: Chỉnh sửa thông tin kỳ thi
     */
    @PutMapping("/{id}")
    public ResponseEntity<Exam> updateExam(@PathVariable Long id, @RequestBody ExamRequest request) {
        return examRepository.findById(id).map(exam -> {
            exam.setTitle(request.getTitle());
            exam.setDescription(request.getDescription());
            exam.setDurationMinutes(request.getDurationMinutes());
            exam.setStatus(request.getStatus());
            exam.setType(request.getType());
            return ResponseEntity.ok(examRepository.save(exam));
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

            QuestionResponse responseDTO = new QuestionResponse();
            responseDTO.setId(savedQuestion.getId());
            responseDTO.setContent(savedQuestion.getContent());

            List<AnswerOptionResponse> optionResponses = savedQuestion.getOptions().stream().map(opt -> {
                AnswerOptionResponse optRes = new AnswerOptionResponse();
                optRes.setId(opt.getId());
                optRes.setContent(opt.getContent());
                return optRes;
            }).collect(Collectors.toList());

            responseDTO.setOptions(optionResponses);

            return ResponseEntity.ok(responseDTO);

        }).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Requirement c: Chỉnh sửa nội dung câu hỏi và các lựa chọn
     */
    @PutMapping("/questions/{questionId}")
    @Transactional
    public ResponseEntity<?> updateQuestion(@PathVariable Long questionId, @RequestBody QuestionRequest request) {
        return questionRepository.findById(questionId).map(question -> {
            question.setContent(request.getContent());
            question.setExplanation(request.getExplanation());

            // Xóa các option cũ và thay bằng list mới (Yêu cầu orphanRemoval = true trong Entity)
            question.getOptions().clear();

            request.getOptions().forEach(optReq -> {
                AnswerOption opt = new AnswerOption();
                opt.setContent(optReq.getContent());
                opt.setIsCorrect(optReq.getIsCorrect());
                opt.setQuestion(question);
                question.getOptions().add(opt);
            });

            Question savedQuestion = questionRepository.save(question);

            QuestionResponse responseDTO = new QuestionResponse();
            responseDTO.setId(savedQuestion.getId());
            responseDTO.setContent(savedQuestion.getContent());

            List<AnswerOptionResponse> optionResponses = savedQuestion.getOptions().stream().map(opt -> {
                AnswerOptionResponse optRes = new AnswerOptionResponse();
                optRes.setId(opt.getId());
                optRes.setContent(opt.getContent());
                return optRes;
            }).collect(Collectors.toList());

            responseDTO.setOptions(optionResponses);

            return ResponseEntity.ok(responseDTO);

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
}