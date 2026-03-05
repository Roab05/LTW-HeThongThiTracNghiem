// DỮ LIỆU GIẢ LẬP (MOCK DATA)

// 1. Dữ liệu Sinh viên
const studentsData = {
    "B21DCCN001": { name: "Nguyễn Văn A", class: "D21CQCN01-B" },
    "B21DCCN002": { name: "Trần Thị B", class: "D21CQCN02-B" },
    "B21DCCN003": { name: "Nguyễn Văn An", class: "D21CQCN01-B" },
    "B21DCCN004": { name: "Nguyễn Tuấn Anh", class: "D21CQCN03-B" }
};

// 2. Lịch sử thi của từng Sinh viên
const historyData = {
    "B21DCCN001": [
        { examId: "EX01", name: "Kiểm tra cuối kì - Triết học", time: "25/02/2026 10:30", score: 8.5, status: "Hoàn thành" },
        { examId: "EX02", name: "Luyện tập quay lui", time: "15/02/2026 14:00", score: 4.0, status: "Chưa đạt" }
    ],
    "B21DCCN002": [
        { examId: "EX01", name: "Kiểm tra cuối kì - Triết học", time: "25/02/2026 09:15", score: 6.0, status: "Hoàn thành" }
    ]
};

// 3. Chi tiết bài làm (Theo mã kỳ thi + mã sinh viên - Giả lập cứng cho dễ demo)
const detailsData = {
    "EX01": [
        {
            question: "Câu 1: Chủ nghĩa Mác nói chung, triết học Mác nói riêng ra đời vào những năm nào của thế kỷ XIX?",
            options: ["A. Những năm 30", "B. Những năm 40", "C. Những năm 50", "D. Những năm 20"],
            correctAnswer: 1, // B
            studentAnswer: 1, // Chọn đúng
            explanation: "Triết học Mác ra đời vào những năm 40 của thế kỷ XIX, gắn liền với sự phát triển mạnh mẽ của phương thức sản xuất tư bản chủ nghĩa."
        },
        {
            question: "Câu 2: Quan điểm nào cho rằng: mọi sự vật, hiện tượng chỉ là “phức hợp những cảm giác”?",
            options: ["A. Chủ nghĩa duy tâm chủ quan", "B. Chủ nghĩa duy tâm khách quan", "C. Chủ nghĩa duy vật siêu hình", "D. Chủ nghĩa duy vật biện chứng"],
            correctAnswer: 0, // A
            studentAnswer: 1, // Chọn sai B
            explanation: "Đây là quan điểm kinh điển của G. Berkeley đại diện cho Chủ nghĩa duy tâm chủ quan."
        }
    ],
    "EX02": [
        {
            question: "Câu 1: Thuật toán quay lui (Backtracking) thường được triển khai bằng kỹ thuật lập trình nào?",
            options: ["A. Quy hoạch động", "B. Đệ quy", "C. Tham lam", "D. Duyệt đồ thị BFS"],
            correctAnswer: 1,
            studentAnswer: 1,
            explanation: "Thuật toán quay lui bản chất là duyệt không gian trạng thái, thường dùng Đệ quy để gọi lại hàm với trạng thái mới."
        }
    ]
};

// ======================================
// CÁC HÀM XỬ LÝ GIAO DIỆN
// ======================================

// Thay thế hàm searchStudent cũ bằng đoạn này
function searchStudent() {
    const input = document.getElementById('searchInput').value.trim();
    const inputUpper = input.toUpperCase();
    const inputLower = input.toLowerCase();
    
    const searchResultsSection = document.getElementById('search-results-section');
    const resultArea = document.getElementById('result-area');
    const notFoundMsg = document.getElementById('not-found-msg');
    const detailSection = document.getElementById('exam-detail-section');

    // Ẩn tất cả các vùng khi bắt đầu tìm kiếm mới
    resultArea.style.display = 'none';
    detailSection.style.display = 'none';
    notFoundMsg.style.display = 'none';
    searchResultsSection.style.display = 'none';

    if (!input) {
        alert("Vui lòng nhập từ khóa tìm kiếm!");
        return;
    }

    // Mảng chứa các sinh viên khớp với từ khóa
    let matchedStudents = [];

    // Duyệt qua toàn bộ data để tìm các kết quả khớp (chứa một phần Mã hoặc Tên)
    for (const [id, info] of Object.entries(studentsData)) {
        if (id.includes(inputUpper) || info.name.toLowerCase().includes(inputLower)) {
            matchedStudents.push({ id: id, name: info.name, class: info.class });
        }
    }

    // Xử lý kết quả hiển thị
    if (matchedStudents.length > 0) {
        // Có kết quả -> Hiện bảng danh sách
        searchResultsSection.style.display = 'block';
        const tbody = document.getElementById('search-results-body');
        tbody.innerHTML = '';

        matchedStudents.forEach(student => {
            const row = `
                <tr>
                    <td><strong>${student.id}</strong></td>
                    <td>${student.name}</td>
                    <td>${student.class}</td>
                    <td>
                        <button class="btn-view" onclick="viewStudentProfile('${student.id}')" style="background-color: #d13d3d; color: white;">
                            <i class="fas fa-eye"></i> Xem hồ sơ
                        </button>
                    </td>
                </tr>
            `;
            tbody.insertAdjacentHTML('beforeend', row);
        });
    } else {
        // Không tìm thấy ai
        notFoundMsg.style.display = 'block';
    }
}

// Hàm mới: Hiển thị Profile khi click vào "Xem hồ sơ" từ bảng tìm kiếm
function viewStudentProfile(studentId) {
    const searchResultsSection = document.getElementById('search-results-section');
    const resultArea = document.getElementById('result-area');
    
    // Ẩn bảng kết quả tìm kiếm, hiện vùng profile chi tiết
    searchResultsSection.style.display = 'none';
    resultArea.style.display = 'block';

    // Điền thông tin profile
    document.getElementById('student-name').innerText = studentsData[studentId].name;
    document.getElementById('student-id').innerText = studentId;
    document.getElementById('student-class').innerText = studentsData[studentId].class;

    // Render lịch sử thi
    renderHistoryTable(studentId);
}

// Hàm mới: Quay lại bảng danh sách tìm kiếm
function backToSearchResults() {
    const searchResultsSection = document.getElementById('search-results-section');
    const resultArea = document.getElementById('result-area');
    const detailSection = document.getElementById('exam-detail-section');

    // 1. Ẩn vùng hiển thị hồ sơ cá nhân hiện tại
    resultArea.style.display = 'none';
    
    // 2. Đảm bảo vùng chi tiết bài làm cũng đóng lại
    detailSection.style.display = 'none';

    // 3. Hiện lại bảng danh sách tìm kiếm đã render trước đó
    searchResultsSection.style.display = 'block';
}

function renderHistoryTable(studentId) {
    const tbody = document.getElementById('history-table-body');
    tbody.innerHTML = '';

    const history = historyData[studentId] || [];

    if (history.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" style="text-align:center;">Sinh viên chưa tham gia kỳ thi nào.</td></tr>';
        return;
    }

    history.forEach(item => {
        const badgeClass = item.score >= 5.0 ? 'pass' : 'fail';
        const row = `
            <tr>
                <td><strong>${item.name}</strong></td>
                <td>${item.time}</td>
                <td><strong>${item.score.toFixed(1)}</strong></td>
                <td><span class="badge ${badgeClass}">${item.status}</span></td>
                <td class="no-print">
                    <button class="btn-view" onclick="viewDetails('${item.examId}', '${item.name}', ${item.score}, '${item.time}')">
                        <i class="fas fa-eye"></i> Xem bài làm
                    </button>
                </td>
            </tr>
        `;
        tbody.insertAdjacentHTML('beforeend', row);
    });
}

function viewDetails(examId, examName, score, time) {
    const detailSection = document.getElementById('exam-detail-section');
    detailSection.style.display = 'block';

    // Cập nhật Header chi tiết
    document.getElementById('detail-exam-name').innerText = `Chi tiết bài làm: ${examName}`;
    document.getElementById('detail-score').innerText = score.toFixed(1);
    document.getElementById('detail-time').innerText = time;

    // Render danh sách câu hỏi và đáp án
    const container = document.getElementById('questions-container');
    container.innerHTML = '';

    const qData = detailsData[examId] || [];

    if (qData.length === 0) {
        container.innerHTML = '<p>Dữ liệu chi tiết của bài thi này không khả dụng.</p>';
        // Cuộn xuống
        detailSection.scrollIntoView({ behavior: 'smooth' });
        return;
    }

    qData.forEach((q, index) => {
        let optionsHtml = '';
        
        q.options.forEach((opt, optIdx) => {
            let optClass = '';
            let iconHtml = '';

            if (optIdx === q.correctAnswer) {
                optClass = 'is-correct';
                iconHtml = '<i class="fas fa-check-circle opt-icon"></i>';
            } else if (optIdx === q.studentAnswer && q.studentAnswer !== q.correctAnswer) {
                optClass = 'is-wrong';
                iconHtml = '<i class="fas fa-times-circle opt-icon"></i>';
            }

            optionsHtml += `
                <li class="opt-item ${optClass}">
                    ${opt} ${iconHtml}
                </li>
            `;
        });

        // Báo trạng thái câu trả lời (Đúng/Sai)
        const isUserCorrect = q.studentAnswer === q.correctAnswer;
        const statusText = isUserCorrect ? '<span style="color: #2e7d32;">(Đúng)</span>' : '<span style="color: #c62828;">(Sai)</span>';

        const qHtml = `
            <div class="q-detail-block">
                <div class="q-title">${q.question} ${statusText}</div>
                <ul class="q-options">
                    ${optionsHtml}
                </ul>
                <div class="q-explanation">
                    <strong><i class="fas fa-lightbulb"></i> Giải thích:</strong> ${q.explanation}
                </div>
            </div>
        `;
        container.insertAdjacentHTML('beforeend', qHtml);
    });

    // Cuộn mượt mà xuống vùng chi tiết
    detailSection.scrollIntoView({ behavior: 'smooth' });
}

function closeDetail() {
    document.getElementById('exam-detail-section').style.display = 'none';
}

// Hỗ trợ ấn Enter để tìm kiếm
document.getElementById('searchInput').addEventListener('keypress', function(e) {
    if (e.key === 'Enter') {
        searchStudent();
    }
});

// BỔ SUNG: Tự động bắt URL Parameter và hiển thị luôn kết quả khi trang vừa tải xong
window.onload = function() {
    // Lấy tham số '?id=...' từ trên thanh địa chỉ URL
    const urlParams = new URLSearchParams(window.location.search);
    const studentIdFromUrl = urlParams.get('id');

    // Nếu có truyền ID sang, tự động điền vào ô tìm kiếm và gọi hàm search
    if (studentIdFromUrl) {
        document.getElementById('searchInput').value = studentIdFromUrl;
        searchStudent();
    }
};