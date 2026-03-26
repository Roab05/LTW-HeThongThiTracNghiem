package ltw.examsystem.service;

import ltw.examsystem.dto.request.QuestionRequest;
import ltw.examsystem.dto.response.QuestionResponse;

public interface QuestionService {

    QuestionResponse addQuestion(Long examId, QuestionRequest request);
    QuestionResponse updateQuestion(Long questionId, QuestionRequest request);

    void deleteQuestion(Long questionId);
}
