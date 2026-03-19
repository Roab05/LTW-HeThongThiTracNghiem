package ltw.examsystem.service;

import ltw.examsystem.entity.Submission;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ExcelService {
    // Hàm xử lý logic đọc file Excel và lưu vào Database
    void importQuestionsFromExcel(Long examId, MultipartFile file) throws Exception;

    byte[] exportSubmissionsToExcel(List<Submission> submissions) throws IOException;
}