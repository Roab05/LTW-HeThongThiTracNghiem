package ltw.examsystem.service.impl;

import ltw.examsystem.entity.AnswerOption;
import ltw.examsystem.entity.Exam;
import ltw.examsystem.entity.Question;
import ltw.examsystem.entity.Submission;
import ltw.examsystem.repository.ExamRepository;
import ltw.examsystem.repository.QuestionRepository;
import ltw.examsystem.service.ExcelService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ExcelServiceImpl implements ExcelService {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Override
    @Transactional
    public void importQuestionsFromExcel(Long examId, MultipartFile file) throws Exception {
        // 1. Kiểm tra kỳ thi có tồn tại không
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kỳ thi với ID: " + examId));

        // 2. Đọc file Excel từ InputStream
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0); // Lấy sheet đầu tiên
            Iterator<Row> rows = sheet.iterator();

            // Bỏ qua dòng tiêu đề (Header row)
            if (rows.hasNext()) {
                rows.next();
            }

            // 3. Duyệt từng dòng để lấy dữ liệu câu hỏi
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                // Đọc nội dung câu hỏi (Cột 0) và Giải thích (Cột 1)
                String questionContent = getCellValue(currentRow.getCell(0));
                String explanation = getCellValue(currentRow.getCell(1));

                if (questionContent.isEmpty()) continue;

                Question question = new Question();
                question.setContent(questionContent);
                question.setExplanation(explanation); // Đáp ứng Requirement d
                question.setExam(exam);

                // 4. Đọc các lựa chọn (Cột 2, 3, 4, 5 tương ứng A, B, C, D)
                String optA = getCellValue(currentRow.getCell(2));
                String optB = getCellValue(currentRow.getCell(3));
                String optC = getCellValue(currentRow.getCell(4));
                String optD = getCellValue(currentRow.getCell(5));

                // Đọc đáp án đúng (Cột 6: Chứa chữ "A", "B", "C" hoặc "D")
                String correctLabel = getCellValue(currentRow.getCell(6)).trim().toUpperCase();

                List<AnswerOption> options = new ArrayList<>();
                options.add(createOption(optA, "A".equals(correctLabel), question));
                options.add(createOption(optB, "B".equals(correctLabel), question));
                options.add(createOption(optC, "C".equals(correctLabel), question));
                options.add(createOption(optD, "D".equals(correctLabel), question));

                question.setOptions(options);

                // 5. Lưu câu hỏi (Cascade sẽ lưu luôn các AnswerOption)
                questionRepository.save(question);
            }
        }
    }

    @Override
    public byte[] exportSubmissionsToExcel(List<Submission> submissions) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Ket_Qua_Thi");

            // 1. Tạo Header (Dòng tiêu đề)
            Row headerRow = sheet.createRow(0);
            String[] columns = {"STT", "Sinh viên", "Kỳ thi", "Điểm số", "Số câu đúng", "Thời gian nộp"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                // (Tùy chọn) Thêm style cho header đậm lên
            }

            // 2. Đổ dữ liệu từ danh sách vào các dòng tiếp theo
            int rowIdx = 1;
            for (Submission s : submissions) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(rowIdx - 1);
                row.createCell(1).setCellValue(s.getUser().getUsername());
                row.createCell(2).setCellValue(s.getExam().getTitle());
                row.createCell(3).setCellValue(s.getScore());
                row.createCell(4).setCellValue(s.getCorrectAnswers() + "/" + s.getTotalQuestions());
                row.createCell(5).setCellValue(s.getSubmitTime().toString());
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    // Hàm bổ trợ tạo đối tượng AnswerOption
    private AnswerOption createOption(String content, boolean isCorrect, Question question) {
        AnswerOption option = new AnswerOption();
        option.setContent(content);
        option.setIsCorrect(isCorrect);
        option.setQuestion(question);
        return option;
    }

    // Hàm bổ trợ đọc giá trị của ô (Cell) bất kể kiểu dữ liệu là gì
    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC: return String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            default: return "";
        }
    }
}