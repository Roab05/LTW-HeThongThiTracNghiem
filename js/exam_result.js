// DỮ LIỆU GIẢ LẬP (MOCK DATA)
const resultData = [
    {
        id: 1,
        text: "Chủ nghĩa Mác nói chung, triết học Mác nói riêng ra đời vào những năm nào của thế kỷ XIX:",
        options: ["Những năm 30 của thế kỷ XIX", "Những năm 40 của thế kỷ XIX", "Những năm 50 của thế kỷ XIX", "Những năm 20 của thế kỷ XIX"],
        correctAnswer: 1, // B
        userAnswer: 1     // Người dùng chọn đúng B
    },
    {
        id: 2,
        text: "Quan điểm nào cho rằng: mọi sự vật, hiện tượng chỉ là “phức hợp những cảm giác” của con người, của chủ thể nhận thức là quan điểm thể hiện lập trường triết học nào:",
        options: ["Chủ nghĩa duy tâm chủ quan", "Chủ nghĩa duy tâm khách quan", "Chủ nghĩa duy vật siêu hình", "Chủ nghĩa duy vật biện chứng"],
        correctAnswer: 0, // A
        userAnswer: 1     // Người dùng chọn sai B
    },
    {
        id: 3,
        text: "Hãy cho biết, trong những vấn đề dưới đây, việc giải quyết vấn đề nào sẽ là tiêu chuẩn để xác định lập trường thế giới quan của các triết gia và học thuyết của họ:",
        options: ["Vấn đề giai cấp", "Vấn đề con người", "Vấn đề dân tộc", "Vấn đề cơ bản của triết học"],
        correctAnswer: 3, // D
        userAnswer: null  // Bỏ qua (không làm)
    },
    {
        id: 4,
        text: "Hệ thống triết học nào quan niệm sự vật là phức hợp của các cảm giác?",
        options: ["Chủ nghĩa duy vật siêu hình", "Chủ nghĩa duy tâm khách quan", "Chủ nghĩa duy tâm chủ quan", "Chủ nghĩa duy vật biện chứng"],
        correctAnswer: 2, // C
        userAnswer: 2     // Đúng C
    }
];

function calculateAndRender() {
    let total = resultData.length;
    let correct = 0;
    let wrong = 0;
    let skipped = 0;

    // Tính toán
    resultData.forEach(q => {
        if (q.userAnswer === null) {
            skipped++;
        } else if (q.userAnswer === q.correctAnswer) {
            correct++;
        } else {
            wrong++;
        }
    });

    // Điểm số thang điểm 10
    const score = (correct / total) * 10;
    const accuracy = (correct / total) * 100;

    // Render Thống kê
    document.getElementById('final-score').innerText = score.toFixed(1); // VD: 5.0
    document.getElementById('stat-correct').innerText = correct;
    document.getElementById('stat-wrong').innerText = wrong;
    document.getElementById('stat-skipped').innerText = skipped;
    document.getElementById('stat-total').innerText = total;
    
    // Render Progress bar
    setTimeout(() => {
        document.getElementById('accuracy-bar').style.width = accuracy + '%';
    }, 300); // Delay nhẹ tạo hiệu ứng chạy thanh progress
    document.getElementById('accuracy-text').innerText = `Tỷ lệ đúng: ${Math.round(accuracy)}%`;

    // Render Chi tiết câu hỏi
    renderDetails();
}

function renderDetails() {
    const container = document.getElementById('review-container');
    const letters = ['A', 'B', 'C', 'D'];

    resultData.forEach((q, index) => {
        // Xác định trạng thái câu hỏi
        let statusClass = '';
        let statusText = '';
        if (q.userAnswer === null) {
            statusClass = 'status-skipped';
            statusText = 'Bỏ qua';
        } else if (q.userAnswer === q.correctAnswer) {
            statusClass = 'status-correct';
            statusText = 'Đúng';
        } else {
            statusClass = 'status-incorrect';
            statusText = 'Sai';
        }

        // Tạo HTML cho các đáp án
        let optionsHtml = '';
        q.options.forEach((opt, optIdx) => {
            let optClass = '';
            let iconHtml = '';

            if (optIdx === q.correctAnswer) {
                // Đây là đáp án đúng -> Luôn bôi xanh
                optClass = 'is-correct';
                iconHtml = '<i class="fas fa-check opt-icon"></i>';
            } else if (optIdx === q.userAnswer && q.userAnswer !== q.correctAnswer) {
                // Đây là đáp án người dùng chọn NHƯNG sai -> Bôi đỏ
                optClass = 'is-wrong';
                iconHtml = '<i class="fas fa-times opt-icon"></i>';
            }

            optionsHtml += `
                <li class="opt-item ${optClass}">
                    <div class="opt-circle">${letters[optIdx]}</div>
                    <span class="opt-text">${opt}</span>
                    ${iconHtml}
                </li>
            `;
        });

        // Ghép thành khối câu hỏi
        const qHtml = `
            <div class="review-question-block">
                <div class="q-header">
                    <span class="q-status ${statusClass}">${statusText}</span>
                    <span class="q-text">Câu ${q.id}: ${q.text}</span>
                </div>
                <ul class="review-options">
                    ${optionsHtml}
                </ul>
            </div>
        `;
        container.insertAdjacentHTML('beforeend', qHtml);
    });
}

// Chạy khởi tạo khi load trang
calculateAndRender();