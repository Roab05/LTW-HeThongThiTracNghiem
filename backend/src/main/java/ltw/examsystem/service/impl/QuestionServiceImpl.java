package ltw.examsystem.service.impl;

import ltw.examsystem.dto.request.AnswerOptionRequest;
import ltw.examsystem.dto.request.QuestionRequest;
import ltw.examsystem.dto.response.AnswerOptionResponse;
import ltw.examsystem.dto.response.QuestionResponse;
import ltw.examsystem.entity.AnswerOption;
import ltw.examsystem.entity.Exam;
import ltw.examsystem.entity.Question;
import ltw.examsystem.repository.ExamRepository;
import ltw.examsystem.repository.QuestionRepository;
import ltw.examsystem.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private ExamRepository examRepository;

    @Override
    @Transactional
    public QuestionResponse addQuestion(Long examId, QuestionRequest request) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy kỳ thi"));

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

        Question saved = questionRepository.save(question);
        return convertToQuestionDto(saved);
    }

    @Override
    @Transactional
    public QuestionResponse updateQuestion(Long questionId, QuestionRequest request) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy câu hỏi"));

        question.setContent(request.getContent());
        question.setExplanation(request.getExplanation());

        List<AnswerOption> dbOptions = question.getOptions();
        List<AnswerOptionRequest> reqOptions = request.getOptions();

        // Logic đồng bộ đáp án (Giữ nguyên từ code cũ của bạn nhưng nằm ở Service)
        for (int i = 0; i < reqOptions.size(); i++) {
            AnswerOptionRequest optReq = reqOptions.get(i);
            if (i < dbOptions.size()) {
                dbOptions.get(i).setContent(optReq.getContent());
                dbOptions.get(i).setIsCorrect(optReq.getIsCorrect());
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
        return convertToQuestionDto(saved);
    }

    @Override
    public void deleteQuestion(Long questionId) {
        if (!questionRepository.existsById(questionId)) {
            throw new IllegalArgumentException("Không tìm thấy câu hỏi với ID: " + questionId);
        }
        questionRepository.deleteById(questionId);
    }

    // Copy hàm convertToQuestionDto từ Controller sang đây
    private QuestionResponse convertToQuestionDto(Question q) {
        QuestionResponse dto = new QuestionResponse();
        dto.setId(q.getId());
        dto.setContent(q.getContent());
        dto.setExplanation(q.getExplanation());

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
}