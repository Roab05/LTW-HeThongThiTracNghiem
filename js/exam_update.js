let questions = [];

function toggleTimeInput() {
    const type = document.getElementById('exam-type').value;
    const timeSetting = document.getElementById('time-setting');
    if (type === 'fixed') {
        timeSetting.style.display = 'block';
    } else {
        timeSetting.style.display = 'none';
    }
}

function renderQuestions() {
    const container = document.getElementById('questions-container');
    container.innerHTML = '';

    if (questions.length === 0) {
        container.innerHTML = '<p style="text-align: center; color: #888; font-style: italic;">Chưa có câu hỏi nào. Hãy thêm thủ công hoặc nhập từ file Excel.</p>';
        return;
    }

    questions.forEach((q, index) => {
        let optionsHtml = '';
        const letters = ['A', 'B', 'C', 'D'];

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

function addQuestion() {
    questions.push({
        text: "",
        options: ["", "", "", ""],
        correctIndex: 0 
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

function importExcelMock() {
    const fileInput = document.getElementById('excel-upload');
    if(fileInput.files.length > 0) {
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
        fileInput.value = ''; 
    }
}

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

addQuestion();