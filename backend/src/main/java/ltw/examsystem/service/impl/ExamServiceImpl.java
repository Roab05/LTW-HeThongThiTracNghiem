package ltw.examsystem.service.impl;

import ltw.examsystem.dto.response.AnswerOptionResponse;
import ltw.examsystem.dto.response.ExamDetailResponse;
import ltw.examsystem.dto.response.QuestionResponse;
import ltw.examsystem.entity.Exam;
import ltw.examsystem.repository.ExamRepository;
import ltw.examsystem.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class ExamServiceImpl implements ExamService {

    @Autowired
    private ExamRepository examRepository;

    @Override
    public ExamDetailResponse getExamDetailsForStudent(Long examId) {
        // 1. Lấy thông tin Exam từ Database
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy đề thi với ID " + examId));

        // 2. Map dữ liệu sang DTO để trả về cho Client
        ExamDetailResponse response = new ExamDetailResponse();
        response.setId(exam.getId());
        response.setTitle(exam.getTitle());
        response.setDurationMinutes(exam.getDurationMinutes());

        // 3. Map danh sách câu hỏi và loại bỏ cột isCorrect
        response.setQuestions(exam.getQuestions().stream().map(question -> {

            QuestionResponse questionDto = new QuestionResponse();
            questionDto.setId(question.getId());
            questionDto.setContent(question.getContent());

            // Lấy các đáp án A, B, C, D của câu hỏi này
            questionDto.setOptions(question.getOptions().stream().map(option -> {
                AnswerOptionResponse optionDto = new AnswerOptionResponse();
                optionDto.setId(option.getId());
                optionDto.setContent(option.getContent());
                // Cố tình KHÔNG set isCorrect ở đây để sinh viên không xem trộm được
                return optionDto;
            }).collect(Collectors.toList()));

            return questionDto;

        }).collect(Collectors.toList()));

        return response;
    }
}