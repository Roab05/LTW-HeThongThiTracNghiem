package ltw.examsystem.service.impl;

import ltw.examsystem.dto.request.ExamRequest;
import ltw.examsystem.dto.response.*;
import ltw.examsystem.entity.Exam;
import ltw.examsystem.entity.ExamStatus;
import ltw.examsystem.entity.ExamType;
import ltw.examsystem.repository.ExamRepository;
import ltw.examsystem.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
        response.setDescription(exam.getDescription());
        response.setStartTime(exam.getStartTime());
        response.setEndTime(exam.getEndTime());// Bổ sung cho SV xem mô tả

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
        response.setStartTime(exam.getStartTime());
        response.setEndTime(exam.getEndTime());

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

    @Override
    @Transactional
    public ExamSummaryResponse createExam(ExamRequest request) {
        Exam exam = new Exam();
        mapRequestToEntity(request, exam);
        Exam saved = examRepository.save(exam);
        return convertToSummaryDto(saved);
    }

    @Override
    @Transactional
    public ExamSummaryResponse updateExam(Long id, ExamRequest request) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy kỳ thi với ID: " + id));

        mapRequestToEntity(request, exam);
        Exam saved = examRepository.save(exam);
        return convertToSummaryDto(saved);
    }

    @Override
    public void deleteExam(Long id) {
        if (!examRepository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy kỳ thi với ID: " + id);
        }
        examRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ExamSummaryResponse togglePublish(Long id, boolean publish) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy kỳ thi với ID: " + id));

        exam.setIsPublished(publish);
        Exam saved = examRepository.save(exam);

        String status = publish ? "MỞ" : "ĐÓNG";
        System.out.println("✅ Đề thi ID " + id + " đã được " + status);

        return convertToSummaryDto(saved);
    }

    @Override
    public List<ExamSummaryResponse> getAllExams() {
        // Lấy toàn bộ Entity từ DB
        List<Exam> exams = examRepository.findAll();

        // Dùng stream() để chuyển đổi (map) từng Entity sang DTO
        return exams.stream()
                .map(this::convertToSummaryDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExamSummaryResponse> getExamsForStudent(String title, ExamStatus status, ExamType type) {
        List<Exam> exams = examRepository.findExamsForStudent(title, status, type);
        return exams.stream()
                .map(this::convertToSummaryDto) // Dùng lại hàm convertToSummaryDto bạn đã có sẵn ở Service
                .collect(Collectors.toList());
    }

    // 🚀 COPY HÀM NÀY TỪ CONTROLLER SANG VÀ SỬA THÀNH PRIVATE DÙNG NỘI BỘ
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

    // 🚀 ĐƯA LUÔN HÀM CONVERT DTO XUỐNG ĐÂY ĐỂ ĐỒNG BỘ
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
}