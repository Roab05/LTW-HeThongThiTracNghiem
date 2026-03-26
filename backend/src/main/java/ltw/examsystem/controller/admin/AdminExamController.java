package ltw.examsystem.controller.admin;

import ltw.examsystem.dto.admin.AnswerOptionRequest;
import ltw.examsystem.dto.admin.ExamRequest;
import ltw.examsystem.dto.admin.QuestionRequest;
import ltw.examsystem.dto.admin.AnswerOptionResponse;
import ltw.examsystem.dto.admin.ExamDetailResponse;
import ltw.examsystem.dto.admin.ExamSummaryResponse;
import ltw.examsystem.dto.admin.QuestionResponse;
import ltw.examsystem.entity.AnswerOption;
import ltw.examsystem.entity.Exam;
import ltw.examsystem.entity.Question;
import ltw.examsystem.repository.ExamRepository;
import ltw.examsystem.repository.QuestionRepository;
import ltw.examsystem.service.ExamService;
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
    private ExamService examService;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @GetMapping
    public ResponseEntity<List<ExamSummaryResponse>> getAllExams() {
        List<Exam> exams = examRepository.findAll();
        return ResponseEntity.ok(exams.stream().map(this::convertToSummaryDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExamDetailResponse> getExamDetail(@PathVariable Long id) {
        return ResponseEntity.ok(examService.getExamDetailsForAdmin(id));
    }

    @PostMapping
    public ResponseEntity<ExamSummaryResponse> createExam(@RequestBody ExamRequest request) {
        Exam exam = new Exam();
        mapRequestToEntity(request, exam);
        Exam saved = examRepository.save(exam);
        return ResponseEntity.ok(convertToSummaryDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExamSummaryResponse> updateExam(@PathVariable Long id, @RequestBody ExamRequest request) {
        return examRepository.findById(id).map(exam -> {
            mapRequestToEntity(request, exam);
            return ResponseEntity.ok(convertToSummaryDto(examRepository.save(exam)));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExam(@PathVariable Long id) {
        examRepository.deleteById(id);
        return ResponseEntity.ok("Đã xóa kỳ thi thành công");
    }

    @PatchMapping("/{id}/publish")
    public ResponseEntity<ExamSummaryResponse> togglePublish(
            @PathVariable Long id,
            @RequestParam boolean publish) {

        return examRepository.findById(id).map(exam -> {
            exam.setIsPublished(publish);
            Exam saved = examRepository.save(exam);

            String status = publish ? "Mở" : "Đóng";
            System.out.println("✅ Đề thi ID " + id + " đã được " + status);

            return ResponseEntity.ok(convertToSummaryDto(saved));
        }).orElse(ResponseEntity.notFound().build());
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

            return ResponseEntity.ok(convertToQuestionDto(savedQuestion));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/questions/{questionId}")
    @Transactional
    public ResponseEntity<QuestionResponse> updateQuestion(@PathVariable Long questionId, @RequestBody QuestionRequest request) {
        return questionRepository.findById(questionId).map(question -> {
            question.setContent(request.getContent());
            question.setExplanation(request.getExplanation());

            List<AnswerOption> dbOptions = question.getOptions();
            List<AnswerOptionRequest> reqOptions = request.getOptions();

            for (int i = 0; i < reqOptions.size(); i++) {
                AnswerOptionRequest optReq = reqOptions.get(i);

                if (i < dbOptions.size()) {
                    AnswerOption existingOpt = dbOptions.get(i);
                    existingOpt.setContent(optReq.getContent());
                    existingOpt.setIsCorrect(optReq.getIsCorrect());
                } else {
                    AnswerOption newOpt = new AnswerOption();
                    newOpt.setContent(optReq.getContent());
                    newOpt.setIsCorrect(optReq.getIsCorrect());
                    newOpt.setQuestion(question);
                    dbOptions.add(newOpt);
                }
            }

            if (dbOptions.size() > reqOptions.size()) {
                int currentSize = dbOptions.size();
                for (int i = currentSize - 1; i >= reqOptions.size(); i--) {
                    dbOptions.remove(i);
                }
            }

            Question saved = questionRepository.save(question);
            return ResponseEntity.ok(convertToQuestionDto(saved));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long questionId) {
        if (!questionRepository.existsById(questionId)) {
            return ResponseEntity.notFound().build();
        }
        questionRepository.deleteById(questionId);
        return ResponseEntity.ok("successed");
    }

    private ExamSummaryResponse convertToSummaryDto(Exam exam) {
        ExamSummaryResponse dto = new ExamSummaryResponse();
        dto.setId(exam.getId());
        dto.setTitle(exam.getTitle());
        dto.setDescription(exam.getDescription());
        dto.setType(exam.getType());
        dto.setStatus(exam.getStatus());
        dto.setDurationMinutes(exam.getDurationMinutes());
        dto.setIsPublished(exam.getIsPublished());
        dto.setStartTime(exam.getStartTime());
        dto.setEndTime(exam.getEndTime());
        return dto;
    }

    private QuestionResponse convertToQuestionDto(Question q) {
        QuestionResponse dto = new QuestionResponse();
        dto.setId(q.getId());
        dto.setContent(q.getContent());
        dto.setExplanation(q.getExplanation());


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
        if (ltw.examsystem.entity.ExamStatus.TIME_RESTRICTED.equals(request.getStatus())) {
            if (request.getStartTime() == null || request.getEndTime() == null) {
                throw new IllegalArgumentException("Kỳ thi có giới hạn thời gian bắt buộc phải có thời gian bắt đầu và kết thúc.");
            }
            if (request.getStartTime().isAfter(request.getEndTime())) {
                throw new IllegalArgumentException("Thời gian bắt đầu không được lớn hơn thời gian kết thúc.");
            }
            exam.setStartTime(request.getStartTime());
            exam.setEndTime(request.getEndTime());
        } else {
            exam.setStartTime(null);
            exam.setEndTime(null);
        }
    }
}

