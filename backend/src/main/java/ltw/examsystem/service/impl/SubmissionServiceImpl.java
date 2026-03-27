package ltw.examsystem.service.impl;

import ltw.examsystem.dto.admin.AnswerOptionResponse;
import ltw.examsystem.dto.admin.SubmissionHistoryResponse;
import ltw.examsystem.dto.student.*;
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


    @Transactional
    public SubmissionResultResponse submitExam(Long userId, Long submissionId, SubmitExamRequest request) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiên làm bài này"));

        if(!submission.getUser().getId().equals(userId)){
            throw new RuntimeException("Bài thi này không phải của bạn làm.");
        }

        if (submission.getSubmitTime() != null || submission.getStatus() != SubmissionStatus.IN_PROGRESS) {
            throw new RuntimeException("Bài thi này đã được nộp trước đó.");
        }

        Exam exam = submission.getExam();
        LocalDateTime now = LocalDateTime.now();
        submission.setSubmitTime(now);

        long limitSeconds = (exam.getDurationMinutes() * 60) + 30;
        long actualDurationSeconds = java.time.Duration.between(submission.getStartTime(), now).getSeconds();
        boolean isOvertime = actualDurationSeconds > limitSeconds;

        if (Boolean.TRUE.equals(request.getIsAutoSubmit()) || isOvertime) {
            submission.setStatus(SubmissionStatus.TIMEOUT);
        } else {
            submission.setStatus(SubmissionStatus.SUBMITTED);
        }

        int correctCount = 0;
        int totalQuestions = exam.getQuestions().size();

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

        double score = totalQuestions > 0 ? ((double) correctCount / totalQuestions) * 10 : 0;
        score = Math.round(score * 100.0) / 100.0;

        submission.setCorrectAnswers(correctCount);
        submission.setTotalQuestions(totalQuestions);
        submission.setScore(score);

        submissionRepository.save(submission);

        SubmissionResultResponse response = new SubmissionResultResponse();
        response.setSubmissionId(submission.getId());
        response.setExamTitle(exam.getTitle());
        response.setFullName(submission.getUser().getFullName());
        response.setStudentId(submission.getUser().getStudentId());
        response.setSubmitTime(submission.getSubmitTime());
        response.setScore(score);
        response.setCorrectAnswers(correctCount);
        response.setTotalQuestions(totalQuestions);

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
    public TimeLeftResponse getTimeLeft(Long userId, Long submissionId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Phiên làm bài không tồn tại"));

        if (!submission.getUser().getId().equals(userId)) {
            throw new RuntimeException("Truy cập bị từ chối!");
        }

        Exam exam = submission.getExam();
        LocalDateTime startTime = submission.getStartTime();
        LocalDateTime now = LocalDateTime.now();

        long durationSeconds = exam.getDurationMinutes() * 60L;
        LocalDateTime endTime = startTime.plusSeconds(durationSeconds);

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

    @Transactional
    public Long startExam(Long userId, StartExamRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên"));

        Exam exam = examRepository.findById(request.getExamId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kỳ thi"));

        Submission submission = new Submission();
        submission.setUser(user);
        submission.setExam(exam);
        submission.setStartTime(LocalDateTime.now());
        submission.setStatus(SubmissionStatus.IN_PROGRESS);

        submission = submissionRepository.save(submission);
        return submission.getId();
    }

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
            dto.setFullName(sub.getUser().getFullName());
            dto.setStudentId(sub.getUser().getStudentId());
            if (sub.getSubmitTime() != null) {
                dto.setStatus("Hoàn thành");
            } else {
                dto.setStatus("Không hoàn thành/Đang làm");
            }

            return dto;
        }).collect(Collectors.toList());
    }
    @Override
    public SubmissionDetailResponse getSubmissionDetail(Long submissionId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài làm này"));

        SubmissionDetailResponse resp = new SubmissionDetailResponse();
        resp.setSubmissionId(submission.getId());
        resp.setExamTitle(submission.getExam().getTitle());
        resp.setScore(submission.getScore());
        resp.setSubmitTime(submission.getSubmitTime());
        resp.setFullName(submission.getUser().getFullName());
        resp.setStudentId(submission.getUser().getStudentId());
        List<QuestionResultResponse> details = submission.getDetails().stream().map(detail -> {
            Question q = detail.getQuestion();
            QuestionResultResponse qDto = new QuestionResultResponse();
            qDto.setQuestionId(q.getId());
            qDto.setContent(q.getContent());
            qDto.setExplanation(q.getExplanation());

            qDto.setOptions(q.getOptions().stream().map(opt -> {
                AnswerOptionResponse optDto = new AnswerOptionResponse();
                optDto.setId(opt.getId());
                optDto.setContent(opt.getContent());
                optDto.setIsCorrect(opt.getIsCorrect());
                return optDto;
            }).collect(Collectors.toList()));

            qDto.setSelectedOptionId(detail.getSelectedOption() != null ? detail.getSelectedOption().getId() : null);

            Long correctId = q.getOptions().stream()
                    .filter(o -> Boolean.TRUE.equals(o.getIsCorrect()))
                    .map(AnswerOption::getId)
                    .findFirst()
                    .orElse(null);
            qDto.setCorrectOptionId(correctId);

            qDto.setIsCorrect(qDto.getSelectedOptionId() != null && qDto.getSelectedOptionId().equals(correctId));

            return qDto;
        }).collect(Collectors.toList());

        resp.setQuestionResults(details);
        return resp;
    }
    @Override
    public SubmissionDetailResponse getSubmissionDetail(Long userId, Long submissionId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiên làm bài này"));

        if (!submission.getUser().getId().equals(userId)) {
            throw new RuntimeException("Truy cập bị từ chối! Bạn không có quyền xem bài thi của người khác.");
        }

        return this.getSubmissionDetail(submissionId);
    }
}
