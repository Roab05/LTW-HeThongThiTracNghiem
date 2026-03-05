// Dữ liệu câu hỏi giống hệt ảnh mẫu
const examQuestions = [
    {
        id: 1,
        text: "Chủ nghĩa Mác nói chung, triết học Mác nói riêng ra đời vào những năm nào của thế kỷ XIX:",
        options: ["Những năm 30 của thế kỷ XIX", "Những năm 40 của thế kỷ XIX", "Những năm 50 của thế kỷ XIX", "Những năm 20 của thế kỷ XIX"]
    },
    {
        id: 2,
        text: "Quan điểm nào cho rằng: mọi sự vật, hiện tượng chỉ là “phức hợp những cảm giác” của con người, của chủ thể nhận thức là quan điểm thể hiện lập trường triết học nào:",
        options: ["Chủ nghĩa duy tâm chủ quan", "Chủ nghĩa duy tâm khách quan", "Chủ nghĩa duy vật siêu hình", "Chủ nghĩa duy vật biện chứng"]
    },
    {
        id: 3,
        text: "Hãy cho biết, trong những vấn đề dưới đây, việc giải quyết vấn đề nào sẽ là tiêu chuẩn để xác định lập trường thế giới quan của các triết gia và học thuyết của họ:",
        options: ["Vấn đề giai cấp", "Vấn đề con người", "Vấn đề dân tộc", "Vấn đề cơ bản của triết học"]
    }
];

// Tạo thêm các câu hỏi trống để lấp đầy grid cho giống ảnh
for (let i = 4; i <= 20; i++) {
    examQuestions.push({
        id: i,
        text: `Nội dung câu hỏi số ${i} sẽ hiển thị ở đây...`,
        options: ["Đáp án A", "Đáp án B", "Đáp án C", "Đáp án D"]
    });
}

const userAnswers = {
    1: 0, 
    2: 1
}; // Lưu trữ đáp án của người dùng

const flaggedQuestions = new Set(); // Lưu trữ các câu được cắm cờ
let currentActiveId = 3; // Giả sử câu 3 đang được xem (theo ảnh mẫu)

// Hàm Render nội dung câu hỏi ra màn hình
function renderQuestions() {
    const container = document.getElementById('questions-container');
    container.innerHTML = '';

    examQuestions.forEach(q => {
        const letters = ['A', 'B', 'C', 'D'];
        
        let optionsHtml = '';
        q.options.forEach((opt, index) => {
            const isSelected = userAnswers[q.id] === index ? 'selected' : '';
            optionsHtml += `
                <li class="option-item ${isSelected}" onclick="selectAnswer(${q.id}, ${index})">
                    <div class="option-circle">${letters[index]}</div>
                    <span class="option-text">${opt}</span>
                </li>
            `;
        });

        // Set icon cờ tuỳ trạng thái (câu 3 là cờ đen đặc theo ảnh mẫu)
        const flagClass = flaggedQuestions.has(q.id) || q.id === 3 ? 'fa-solid' : 'fa-regular';

        const qHtml = `
            <div class="question-block" id="q${q.id}">
                <div class="question-text">
                    <button class="flag-btn" onclick="toggleFlag(${q.id})">
                        <i class="${flagClass} fa-flag"></i>
                    </button>
                    <div><span class="question-title">Câu ${q.id}:</span> ${q.text}</div>
                </div>
                <ul class="options-list" id="options-${q.id}">
                    ${optionsHtml}
                </ul>
            </div>
        `;
        container.insertAdjacentHTML('beforeend', qHtml);
    });
}

// Hàm Render Grid ô vuông bên phải
function renderGrid() {
    const grid = document.getElementById('question-grid');
    grid.innerHTML = '';

    examQuestions.forEach(q => {
        let classes = ['grid-item'];
        
        // Mô phỏng trạng thái màu sắc theo ảnh:
        // Câu 1, 2 màu hồng nhạt (answered). Câu 3 màu đỏ đậm (active). Còn lại xám.
        if (q.id === currentActiveId) {
            classes.push('active');
        } else if (userAnswers[q.id] !== undefined || q.id === 1 || q.id === 2) { 
            // Cứng code id 1, 2 thành answered để giống ảnh mẫu
            classes.push('answered');
        }
        
        if (flaggedQuestions.has(q.id)) {
            classes.push('flagged');
        }

        const gridHtml = `<div class="${classes.join(' ')}" onclick="scrollToQuestion(${q.id})">${q.id}</div>`;
        grid.insertAdjacentHTML('beforeend', gridHtml);
    });
}

// Xử lý chọn đáp án
function selectAnswer(questionId, optionIndex) {
    userAnswers[questionId] = optionIndex;
    renderQuestions();
    renderGrid();
}

// Xử lý cắm cờ (Flag)
function toggleFlag(questionId) {
    if (flaggedQuestions.has(questionId)) {
        flaggedQuestions.delete(questionId);
    } else {
        flaggedQuestions.add(questionId);
    }
    renderQuestions();
    renderGrid();
}

// Cuộn tới câu hỏi khi bấm vào Grid
function scrollToQuestion(id) {
    currentActiveId = id;
    renderGrid();
    document.getElementById(`q${id}`).scrollIntoView({ behavior: 'smooth', block: 'center' });
}

// --- LOGIC ĐẾM NGƯỢC THỜI GIAN ---
let totalSeconds = 30 * 60 + 25; // 30 phút 25 giây theo ảnh
const timerDisplay = document.getElementById('countdown');

function updateTimer() {
    if (totalSeconds <= 0) {
        clearInterval(timerInterval);
        alert("HẾT GIỜ! Bài thi của bạn sẽ được tự động nộp.");
        window.location.href = 'exam_result.html';
        return;
    }
    
    totalSeconds--;
    const m = Math.floor(totalSeconds / 60);
    const s = totalSeconds % 60;
    timerDisplay.innerText = `${m.toString().padStart(2, '0')} : ${s.toString().padStart(2, '0')}`;
}
const timerInterval = setInterval(updateTimer, 1000);

// Thay thế hoàn toàn phần xử lý Nộp bài cũ bằng code mới sau:

// 1. Mở Pop-up khi nhấn nút "Nộp bài"
function submitExam() {
    const modal = document.getElementById('submit-modal');
    modal.classList.add('active');
}

// 2. Đóng Pop-up khi nhấn "Hủy" hoặc nút "X"
function closeModal() {
    const modal = document.getElementById('submit-modal');
    modal.classList.remove('active');
}

// 3. Xử lý khi nhấn "Đồng ý nộp"
function confirmSubmit() {
    // Ẩn modal đi
    closeModal();
    
    // Bạn có thể thêm hiệu ứng Loading ở đây trước khi chuyển trang (tùy chọn)
    
    // Chuyển hướng về trang kết quả
    window.location.href = 'exam_result.html'; 
}

// (Tùy chọn) Đóng modal khi click ra ngoài vùng xám
document.getElementById('submit-modal').addEventListener('click', function(e) {
    if (e.target === this) {
        closeModal();
    }
});

// Khởi tạo
renderQuestions();
renderGrid();