const resultData = [
    {
        id: 1,
        text: "Chủ nghĩa Mác nói chung, triết học Mác nói riêng ra đời vào những năm nào của thế kỷ XIX:",
        options: ["Những năm 30 của thế kỷ XIX", "Những năm 40 của thế kỷ XIX", "Những năm 50 của thế kỷ XIX", "Những năm 20 của thế kỷ XIX"],
        correctAnswer: 1, 
        userAnswer: 1 
    },
    {
        id: 2,
        text: "Quan điểm nào cho rằng: mọi sự vật, hiện tượng chỉ là “phức hợp những cảm giác” của con người, của chủ thể nhận thức là quan điểm thể hiện lập trường triết học nào:",
        options: ["Chủ nghĩa duy tâm chủ quan", "Chủ nghĩa duy tâm khách quan", "Chủ nghĩa duy vật siêu hình", "Chủ nghĩa duy vật biện chứng"],
        correctAnswer: 0,
        userAnswer: 1 
    },
    {
        id: 3,
        text: "Hãy cho biết, trong những vấn đề dưới đây, việc giải quyết vấn đề nào sẽ là tiêu chuẩn để xác định lập trường thế giới quan của các triết gia và học thuyết của họ:",
        options: ["Vấn đề giai cấp", "Vấn đề con người", "Vấn đề dân tộc", "Vấn đề cơ bản của triết học"],
        correctAnswer: 3,
        userAnswer: null
    },
    {
        id: 4,
        text: "Hệ thống triết học nào quan niệm sự vật là phức hợp của các cảm giác?",
        options: ["Chủ nghĩa duy vật siêu hình", "Chủ nghĩa duy tâm khách quan", "Chủ nghĩa duy tâm chủ quan", "Chủ nghĩa duy vật biện chứng"],
        correctAnswer: 2,
        userAnswer: 2  
    }
];

function calculateAndRender() {
    let total = resultData.length;
    let correct = 0;
    let wrong = 0;
    let skipped = 0;

    resultData.forEach(q => {
        if (q.userAnswer === null) {
            skipped++;
        } else if (q.userAnswer === q.correctAnswer) {
            correct++;
        } else {
            wrong++;
        }
    });

    const score = (correct / total) * 10;
    const accuracy = (correct / total) * 100;

    document.getElementById('final-score').innerText = score.toFixed(1);
    document.getElementById('stat-correct').innerText = correct;
    document.getElementById('stat-wrong').innerText = wrong;
    document.getElementById('stat-skipped').innerText = skipped;
    document.getElementById('stat-total').innerText = total;

    setTimeout(() => {
        document.getElementById('accuracy-bar').style.width = accuracy + '%';
    }, 300);
    document.getElementById('accuracy-text').innerText = `Tỷ lệ đúng: ${Math.round(accuracy)}%`;

    renderDetails();
}

function renderDetails() {
    const container = document.getElementById('review-container');
    const letters = ['A', 'B', 'C', 'D'];

    resultData.forEach((q, index) => {
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

        let optionsHtml = '';
        q.options.forEach((opt, optIdx) => {
            let optClass = '';
            let iconHtml = '';

            if (optIdx === q.correctAnswer) {
                optClass = 'is-correct';
                iconHtml = '<i class="fas fa-check opt-icon"></i>';
            } else if (optIdx === q.userAnswer && q.userAnswer !== q.correctAnswer) {
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

calculateAndRender();