package ltw.examsystem.service.impl;

import ltw.examsystem.dto.response.*;
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
        // 1. Lấy thông tin Exam từ Database
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy đề thi với ID " + examId));

        // 2. CHẶN BẢO MẬT: Kiểm tra xem đề thi đã được Admin gạt công tắc xuất bản chưa
        if (!Boolean.TRUE.equals(exam.getIsPublished())) {
            throw new RuntimeException("Truy cập bị từ chối! Kỳ thi này hiện chưa được mở hoặc đang bị ẩn.");
        }

        // 3. KIỂM TRA THỜI GIAN: Nếu là kỳ thi có giới hạn thời gian mở/đóng
        if (exam.getStatus() == ExamStatus.TIME_RESTRICTED) {
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            if (exam.getStartTime() != null && now.isBefore(exam.getStartTime())) {
                throw new RuntimeException("Chưa đến giờ làm bài!");
            }
            if (exam.getEndTime() != null && now.isAfter(exam.getEndTime())) {
                throw new RuntimeException("Kỳ thi này đã kết thúc!");
            }
        }

        // 4. Map dữ liệu sang DTO để trả về cho Client
        ExamDetailResponse response = new ExamDetailResponse();
        response.setId(exam.getId());
        response.setTitle(exam.getTitle());
        response.setDurationMinutes(exam.getDurationMinutes());
        response.setDescription(exam.getDescription()); // Bổ sung cho SV xem mô tả

        // 5. Map danh sách câu hỏi và loại bỏ cột isCorrect
        response.setQuestions(exam.getQuestions().stream().map(question -> {

            StudentQuestionResponse questionDto = new StudentQuestionResponse();
            questionDto.setId(question.getId());
            questionDto.setContent(question.getContent());

            // Lấy các đáp án A, B, C, D của câu hỏi này
            questionDto.setOptions(question.getOptions().stream().map(option -> {
                StudentAnswerOptionResponse optionDto = new StudentAnswerOptionResponse();
                optionDto.setId(option.getId());
                optionDto.setContent(option.getContent());
                // Cố tình KHÔNG set isCorrect ở đây để sinh viên không xem trộm được
                return optionDto;
            }).collect(Collectors.toList()));

            return questionDto;

        }).collect(Collectors.toList()));

        return response;
    }

    @Override
    public ExamDetailResponse getExamDetailsForAdmin(Long examId) {
        // 1. Tìm kỳ thi
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kỳ thi với ID: " + examId));

        // 2. Khởi tạo DTO và nạp các trường thông tin cơ bản
        ExamDetailResponse response = new ExamDetailResponse();
        response.setId(exam.getId());
        response.setTitle(exam.getTitle());
        response.setDescription(exam.getDescription());
        response.setDurationMinutes(exam.getDurationMinutes());
        response.setStatus(exam.getStatus().toString());
        response.setType(exam.getType().toString());
        response.setIsPublished(exam.getIsPublished());

        // 3. Map danh sách câu hỏi (Full thông tin cho Admin)
        response.setQuestions(exam.getQuestions().stream().map(q -> {
            QuestionResponse qDto = new QuestionResponse();
            qDto.setId(q.getId());
            qDto.setContent(q.getContent());
            qDto.setExplanation(q.getExplanation()); // Hiện giải thích

            qDto.setOptions(q.getOptions().stream().map(opt -> {
                AnswerOptionResponse optDto = new AnswerOptionResponse();
                optDto.setId(opt.getId());
                optDto.setContent(opt.getContent());
                optDto.setIsCorrect(opt.getIsCorrect()); // Hiện đáp án đúng
                return optDto;
            }).collect(Collectors.toList()));

            return qDto;
        }).collect(Collectors.toList()));

        return response;
    }
}