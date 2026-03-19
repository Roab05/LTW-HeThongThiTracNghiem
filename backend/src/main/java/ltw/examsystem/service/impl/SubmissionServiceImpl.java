package ltw.examsystem.service.impl;

import ltw.examsystem.dto.request.AnswerRequest;
import ltw.examsystem.dto.request.StartExamRequest;
import ltw.examsystem.dto.request.SubmitExamRequest;
import ltw.examsystem.dto.response.*;
import ltw.examsystem.entity.*;
import ltw.examsystem.repository.*;
import ltw.examsystem.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SubmissionServiceImpl implements SubmissionService {

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Requirement c: Xử lý nộp bài (Chủ động hoặc Tự động khi hết giờ)
     */
    @Override
    @Transactional
    public SubmissionResultResponse submitExam(SubmitExamRequest request) {
        // 1. Kiểm tra tồn tại
        Submission submission = submissionRepository.findById(request.getSubmissionId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiên làm bài này"));

        // Nếu bài này đã nộp rồi (có submitTime), có thể chặn không cho nộp lại
        if (submission.getSubmitTime() != null) {
            throw new RuntimeException("Bài thi này đã được nộp trước đó.");
        }

        Exam exam = submission.getExam();
        LocalDateTime now = LocalDateTime.now();
        submission.setSubmitTime(now);

        // 2. Kiểm tra thời gian
        long limitSeconds = (exam.getDurationMinutes() * 60) + 30; // 30s bù trừ độ trễ mạng
        long actualDurationSeconds = java.time.Duration.between(submission.getStartTime(), now).getSeconds();
        boolean isOvertime = actualDurationSeconds > limitSeconds;

        // 3. Chấm điểm
        int correctCount = 0;
        int totalQuestions = exam.getQuestions().size();

        // Xóa chi tiết cũ nếu có (đề phòng nộp đè)
        submission.getDetails().clear();

        Map<Long, Question> questionMap = exam.getQuestions().stream()
                .collect(Collectors.toMap(Question::getId, Function.identity()));

        for (AnswerRequest ansReq : request.getAnswers()) {
            Question question = questionMap.get(ansReq.getQuestionId());
            if (question == null) continue;

            SubmissionDetail detail = new SubmissionDetail();
            detail.setSubmission(submission);
            detail.setQuestion(question);

            if (ansReq.getSelectedOptionId() != null) {
                AnswerOption selectedOpt = question.getOptions().stream()
                        .filter(opt -> opt.getId().equals(ansReq.getSelectedOptionId()))
                        .findFirst()
                        .orElse(null);

                if (selectedOpt != null) {
                    detail.setSelectedOption(selectedOpt);
                    if (Boolean.TRUE.equals(selectedOpt.getIsCorrect())) {
                        correctCount++;
                    }
                }
            }
            submission.getDetails().add(detail);
        }

        // 4. Tính toán điểm số
        double score = totalQuestions > 0 ? ((double) correctCount / totalQuestions) * 10 : 0;
        score = Math.round(score * 100.0) / 100.0;

        submission.setCorrectAnswers(correctCount);
        submission.setTotalQuestions(totalQuestions);
        submission.setScore(score);

        // 5. Lưu xuống DB
        submissionRepository.save(submission);

        // 6. Trả về kết quả
        SubmissionResultResponse response = new SubmissionResultResponse();
        response.setSubmissionId(submission.getId());
        response.setExamTitle(exam.getTitle());
        response.setSubmitTime(submission.getSubmitTime());
        response.setScore(score);
        response.setCorrectAnswers(correctCount);
        response.setTotalQuestions(totalQuestions);

        // Thông báo thông minh hơn
        if (Boolean.TRUE.equals(request.getIsAutoSubmit())) {
            response.setMessage("Hết giờ làm bài! Hệ thống đã tự động ghi nhận kết quả.");
        } else if (isOvertime) {
            response.setMessage("Nộp bài thành công (Lưu ý: Bạn đã nộp quá thời gian quy định).");
        } else {
            response.setMessage("Nộp bài thành công! Chúc mừng bạn đã hoàn thành bài thi.");
        }

        return response;
    }

    @Override
    public TimeLeftResponse getTimeLeft(Long submissionId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Phiên làm bài không tồn tại"));

        Exam exam = submission.getExam();
        LocalDateTime startTime = submission.getStartTime();
        LocalDateTime now = LocalDateTime.now();

        // Tính thời điểm kết thúc = StartTime + Duration
        long durationSeconds = exam.getDurationMinutes() * 60L;
        LocalDateTime endTime = startTime.plusSeconds(durationSeconds);

        // Tính số giây chênh lệch giữa hiện tại và lúc kết thúc
        long secondsLeft = java.time.Duration.between(now, endTime).getSeconds();

        TimeLeftResponse response = new TimeLeftResponse();
        if (secondsLeft <= 0) {
            response.setSecondsLeft(0);
            response.setExpired(true);
        } else {
            response.setSecondsLeft(secondsLeft);
            response.setExpired(false);
        }

        return response;
    }

    @Override
    @Transactional
    public Long startExam(StartExamRequest request) {
        Exam exam = examRepository.findById(request.getExamId())
                .orElseThrow(() -> new RuntimeException("Kỳ thi không tồn tại"));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        // Tạo bản ghi chờ sẵn trong DB
        Submission submission = new Submission();
        submission.setExam(exam);
        submission.setUser(user);
        submission.setStartTime(LocalDateTime.now()); // Lưu thời điểm bắt đầu thật sự

        // Khởi tạo các giá trị mặc định để tránh lỗi null sau này
        submission.setScore(0.0);
        submission.setCorrectAnswers(0);
        submission.setTotalQuestions(exam.getQuestions().size());

        Submission saved = submissionRepository.save(submission);
        return saved.getId(); // Trả về ID để Frontend cầm đi nộp bài
    }

    /**
     * Requirement d: Xem lại lịch sử thi của sinh viên
     */
    @Override
    public List<SubmissionHistoryResponse> getHistoryByUserId(Long userId) {
        List<Submission> history = submissionRepository.findByUserId(userId);
        return history.stream().map(sub -> {
            SubmissionHistoryResponse dto = new SubmissionHistoryResponse();
            dto.setSubmissionId(sub.getId());
            dto.setExamTitle(sub.getExam().getTitle());
            dto.setScore(sub.getScore());
            dto.setCorrectAnswers(sub.getCorrectAnswers());
            dto.setTotalQuestions(sub.getTotalQuestions());
            dto.setSubmitTime(sub.getSubmitTime());

            if (sub.getSubmitTime() != null) {
                dto.setStatus("Hoàn thành");
            } else {
                dto.setStatus("Không hoàn thành/Đang làm");
            }

            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * Requirement d: Xem chi tiết từng câu (Đúng/Sai + Giải thích)
     */
    @Override
    public SubmissionDetailResponse getSubmissionDetail(Long submissionId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài làm này"));

        SubmissionDetailResponse resp = new SubmissionDetailResponse();
        resp.setSubmissionId(submission.getId());
        resp.setExamTitle(submission.getExam().getTitle());
        resp.setScore(submission.getScore());
        resp.setSubmitTime(submission.getSubmitTime());

        List<QuestionResultResponse> details = submission.getDetails().stream().map(detail -> {
            Question q = detail.getQuestion();
            QuestionResultResponse qDto = new QuestionResultResponse();
            qDto.setQuestionId(q.getId());
            qDto.setContent(q.getContent());
            qDto.setExplanation(q.getExplanation()); // Lấy phần giải thích từ DB

            // Map danh sách option của câu hỏi
            qDto.setOptions(q.getOptions().stream().map(opt -> {
                AnswerOptionResponse optDto = new AnswerOptionResponse();
                optDto.setId(opt.getId());
                optDto.setContent(opt.getContent());
                return optDto;
            }).collect(Collectors.toList()));

            // ID đáp án người dùng chọn
            qDto.setSelectedOptionId(detail.getSelectedOption() != null ? detail.getSelectedOption().getId() : null);

            // Tìm ID đáp án đúng thực sự
            Long correctId = q.getOptions().stream()
                    .filter(o -> Boolean.TRUE.equals(o.getIsCorrect()))
                    .map(AnswerOption::getId)
                    .findFirst()
                    .orElse(null);
            qDto.setCorrectOptionId(correctId);

            // Kiểm tra đúng/sai
            qDto.setIsCorrect(qDto.getSelectedOptionId() != null && qDto.getSelectedOptionId().equals(correctId));

            return qDto;
        }).collect(Collectors.toList());

        resp.setQuestionResults(details);
        return resp;
    }
}