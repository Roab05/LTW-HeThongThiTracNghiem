USE exam_system;

-- 1. Thêm người dùng mẫu (Mật khẩu mặc định: 123456 - đã mã hóa BCrypt)
INSERT INTO users (username, email, password) VALUES
('admin', 'admin@gmail.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOn2'),
('sinhvien1', 'sv1@gmail.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOn2');

-- 2. Thêm kỳ thi mẫu
INSERT INTO exams (title, description, duration_minutes, status, type) VALUES
('Lập trình Java Core', 'Kiểm tra kiến thức cơ bản về Java, OOP và Collections.', 15, 'FREE', 'PRACTICE'),
'Lập trình Web nâng cao', 'Đề thi giữa kỳ môn Web, tập trung vào Spring Boot và React.', 45, 'TIME_RESTRICTED', 'MIDTERM');

-- 3. Thêm câu hỏi cho đề Java Core (exam_id = 1)
INSERT INTO questions (content, explanation, exam_id) VALUES
('Java là ngôn ngữ lập trình loại nào?', 'Java là ngôn ngữ hướng đối tượng hoàn toàn (OOP).', 1),
('Tính chất nào sau đây không phải của OOP?', 'Đa luồng là tính năng xử lý, không phải tính chất cơ bản của OOP.', 1);

-- 4. Thêm lựa chọn đáp án cho câu hỏi 1 (question_id = 1)
INSERT INTO answer_options (content, is_correct, question_id) VALUES
('Hướng đối tượng', 1, 1),
('Hướng thủ tục', 0, 1),
('Ngôn ngữ máy', 0, 1),
('Ngôn ngữ đánh dấu', 0, 1);

-- 5. Thêm lựa chọn đáp án cho câu hỏi 2 (question_id = 2)
INSERT INTO answer_options (content, is_correct, question_id) VALUES
('Đóng gói', 0, 2),
('Kế thừa', 0, 2),
('Đa luồng', 1, 2),
('Đa hình', 0, 2);