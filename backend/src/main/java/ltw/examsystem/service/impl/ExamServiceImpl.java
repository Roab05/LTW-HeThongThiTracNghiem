package ltw.examsystem.service.impl;

import ltw.examsystem.dto.admin.AnswerOptionResponse;
import ltw.examsystem.dto.admin.ExamDetailResponse;
import ltw.examsystem.dto.admin.QuestionResponse;
import ltw.examsystem.dto.student.StudentAnswerOptionResponse;
import ltw.examsystem.dto.student.StudentQuestionResponse;
import ltw.examsystem.entity.Exam;
import ltw.examsystem.entity.ExamStatus;
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
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy đề thi với ID " + examId));

        if (!Boolean.TRUE.equals(exam.getIsPublished())) {
            throw new RuntimeException("Truy cập bị từ chối! Kỳ thi này hiện chưa được mở hoặc đang bị ẩn.");
        }

        if (exam.getStatus() == ExamStatus.TIME_RESTRICTED) {
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            if (exam.getStartTime() != null && now.isBefore(exam.getStartTime())) {
                throw new RuntimeException("Chưa đến giờ làm bài!");
            }
            if (exam.getEndTime() != null && now.isAfter(exam.getEndTime())) {
                throw new RuntimeException("Kỳ thi này đã kết thúc!");
            }
        }

        ExamDetailResponse response = new ExamDetailResponse();
        response.setId(exam.getId());
        response.setTitle(exam.getTitle());
        response.setDurationMinutes(exam.getDurationMinutes());
        response.setDescription(exam.getDescription());
        response.setStartTime(exam.getStartTime());
        response.setEndTime(exam.getEndTime());
        response.setQuestions(exam.getQuestions().stream().map(question -> {

            StudentQuestionResponse questionDto = new StudentQuestionResponse();
            questionDto.setId(question.getId());
            questionDto.setContent(question.getContent());

            questionDto.setOptions(question.getOptions().stream().map(option -> {
                StudentAnswerOptionResponse optionDto = new StudentAnswerOptionResponse();
                optionDto.setId(option.getId());
                optionDto.setContent(option.getContent());
                return optionDto;
            }).collect(Collectors.toList()));

            return questionDto;

        }).collect(Collectors.toList()));

        return response;
    }
    @Override
    public ExamDetailResponse getExamDetailsForAdmin(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kỳ thi với ID: " + examId));

        ExamDetailResponse response = new ExamDetailResponse();
        response.setId(exam.getId());
        response.setTitle(exam.getTitle());
        response.setDescription(exam.getDescription());
        response.setDurationMinutes(exam.getDurationMinutes());
        response.setStatus(exam.getStatus().toString());
        response.setType(exam.getType().toString());
        response.setIsPublished(exam.getIsPublished());
        response.setStartTime(exam.getStartTime());
        response.setEndTime(exam.getEndTime());

        response.setQuestions(exam.getQuestions().stream().map(q -> {
            QuestionResponse qDto = new QuestionResponse();
            qDto.setId(q.getId());
            qDto.setContent(q.getContent());
            qDto.setExplanation(q.getExplanation());

            qDto.setOptions(q.getOptions().stream().map(opt -> {
                AnswerOptionResponse optDto = new AnswerOptionResponse();
                optDto.setId(opt.getId());
                optDto.setContent(opt.getContent());
                optDto.setIsCorrect(opt.getIsCorrect());
                return optDto;
            }).collect(Collectors.toList()));

            return qDto;
        }).collect(Collectors.toList()));

        return response;
    }
}
