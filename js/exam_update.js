// Cấu trúc dữ liệu lưu trữ câu hỏi
let questions = [];

// 1. Logic ẩn hiện chọn thời gian
function toggleTimeInput() {
    const type = document.getElementById('exam-type').value;
    const timeSetting = document.getElementById('time-setting');
    if (type === 'fixed') {
        timeSetting.style.display = 'block';
    } else {
        timeSetting.style.display = 'none';
    }
}

// 2. Render danh sách câu hỏi ra màn hình
function renderQuestions() {
    const container = document.getElementById('questions-container');
    container.innerHTML = ''; // Xóa rỗng trước khi vẽ lại

    if (questions.length === 0) {
        container.innerHTML = '<p style="text-align: center; color: #888; font-style: italic;">Chưa có câu hỏi nào. Hãy thêm thủ công hoặc nhập từ file Excel.</p>';
        return;
    }

    questions.forEach((q, index) => {
        let optionsHtml = '';
        const letters = ['A', 'B', 'C', 'D'];

        // Lặp qua 4 đáp án
        q.options.forEach((opt, optIndex) => {
            const isChecked = q.correctIndex === optIndex ? 'checked' : '';
            optionsHtml += `
                <div class="opt-row">
                    <input type="radio" name="correct-${index}" class="opt-radio" ${isChecked} onchange="updateCorrectAnswer(${index}, ${optIndex})" title="Đánh dấu là đáp án đúng">
                    <span style="font-weight: bold; width: 25px;">${letters[optIndex]}.</span>
                    <input type="text" class="form-control opt-text" value="${opt}" placeholder="Nhập nội dung đáp án ${letters[optIndex]}" onchange="updateOptionText(${index}, ${optIndex}, this.value)">
                </div>
            `;
        });

        // Khối HTML của 1 câu hỏi
        const qHtml = `
            <div class="q-block" id="q-${index}">
                <div class="q-header">
                    <h4>Câu ${index + 1}</h4>
                    <button class="btn-delete-q" onclick="deleteQuestion(${index})" title="Xóa câu hỏi"><i class="fas fa-trash"></i></button>
                </div>
                <div class="form-group">
                    <textarea class="form-control" rows="2" placeholder="Nhập nội dung câu hỏi..." onchange="updateQuestionText(${index}, this.value)">${q.text}</textarea>
                </div>
                <div class="options-container">
                    ${optionsHtml}
                </div>
            </div>
        `;
        container.insertAdjacentHTML('beforeend', qHtml);
    });
}

// 3. Các hàm tương tác với Mảng dữ liệu Câu hỏi
function addQuestion() {
    questions.push({
        text: "",
        options: ["", "", "", ""],
        correctIndex: 0 // Mặc định đáp án A đúng
    });
    renderQuestions();
}

function deleteQuestion(index) {
    if(confirm("Bạn muốn xóa câu hỏi này?")) {
        questions.splice(index, 1);
        renderQuestions();
    }
}

function updateQuestionText(index, val) {
    questions[index].text = val;
}

function updateOptionText(qIndex, optIndex, val) {
    questions[qIndex].options[optIndex] = val;
}

function updateCorrectAnswer(qIndex, optIndex) {
    questions[qIndex].correctIndex = optIndex;
}

// 4. Giả lập tính năng Import Excel
function importExcelMock() {
    const fileInput = document.getElementById('excel-upload');
    if(fileInput.files.length > 0) {
        // Giả vờ đọc file và đẩy 2 câu hỏi mẫu vào mảng
        alert("Đã đọc file Excel thành công! Đang thêm câu hỏi...");
        
        questions.push({
            text: "Đây là câu hỏi được import tự động từ Excel số 1?",
            options: ["Đáp án 1", "Đáp án 2", "Đáp án 3", "Đáp án 4"],
            correctIndex: 1
        });
        
        questions.push({
            text: "Trong C++, toán tử nào dùng để cấp phát bộ nhớ động?",
            options: ["malloc", "new", "allocate", "create"],
            correctIndex: 1
        });

        renderQuestions();
        // Reset file input để có thể chọn lại file đó
        fileInput.value = ''; 
    }
}

// 5. Tính năng Lưu toàn bộ Form
function saveExam() {
    const name = document.getElementById('exam-name').value.trim();
    if (!name) {
        alert("Vui lòng nhập Tên bài thi!");
        return;
    }
    if (questions.length === 0) {
        alert("Bài thi cần có ít nhất 1 câu hỏi!");
        return;
    }

    // Ở đây thực tế sẽ dùng API gửi data lên Backend
    console.log("Exam Data to Save:", {
        name: name,
        desc: document.getElementById('exam-desc').value,
        type: document.getElementById('exam-type').value,
        duration: document.getElementById('exam-duration').value,
        startTime: document.getElementById('exam-start-time').value,
        questions: questions
    });

    alert("Lưu kỳ thi thành công!");
    window.location.href = 'admin_dashboard.html';
}

// Khởi tạo 1 câu hỏi trống mặc định khi mới vào trang
addQuestion();