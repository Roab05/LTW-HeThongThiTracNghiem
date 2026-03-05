// Dữ liệu giả lập kết quả thi của sinh viên
const rawData = [
    { studentId: "B21DCCN001", name: "Nguyễn Văn A", exam: "Kiểm tra cuối kì - Triết học", date: "2026-02-25", score: 8.5, type: "Triết học" },
    { studentId: "B21DCCN002", name: "Trần Thị B", exam: "Kiểm tra cuối kì - Triết học", date: "2026-02-25", score: 4.0, type: "Triết học" },
    { studentId: "B21DCCN003", name: "Lê Hoàng C", exam: "Kiểm tra giữa kì - Lập trình C++", date: "2025-12-22", score: 9.0, type: "C++" },
    { studentId: "B21DCCN004", name: "Phạm Văn D", exam: "Kiểm tra cuối kì - Triết học", date: "2026-02-25", score: 6.5, type: "Triết học" },
    { studentId: "B21DCCN005", name: "Hoàng Thị E", exam: "Kiểm tra giữa kì - Lập trình C++", date: "2025-12-22", score: 3.5, type: "C++" },
    { studentId: "B21DCCN006", name: "Vũ Văn F", exam: "Kiểm tra cuối kì - Triết học", date: "2026-02-25", score: 7.0, type: "Triết học" },
    { studentId: "B21DCCN007", name: "Ngô Thị G", exam: "Kiểm tra giữa kì - Lập trình C++", date: "2025-12-22", score: 8.0, type: "C++" }
];

let currentChart = null;

// Hàm chính: Tính toán, render thẻ, bảng và biểu đồ dựa trên dữ liệu truyền vào
function updateDashboard(dataToRender) {
    // 1. Tính toán thẻ thống kê
    const total = dataToRender.length;
    let sumScore = 0;
    let passedCount = 0;

    // Phân loại điểm cho biểu đồ: <4, 4-6, 6-8, 8-10
    let dist = { '< 4': 0, '4 - 6': 0, '6 - 8': 0, '8 - 10': 0 };

    dataToRender.forEach(item => {
        sumScore += item.score;
        if (item.score >= 5.0) passedCount++; // Giả sử >=5 là qua môn/hoàn thành

        if (item.score < 4) dist['< 4']++;
        else if (item.score < 6) dist['4 - 6']++;
        else if (item.score < 8) dist['6 - 8']++;
        else dist['8 - 10']++;
    });

    const avg = total > 0 ? (sumScore / total).toFixed(2) : 0;
    const rate = total > 0 ? ((passedCount / total) * 100).toFixed(1) : 0;

    // Cập nhật DOM thẻ thống kê
    document.getElementById('total-participants').innerText = total;
    document.getElementById('completion-rate').innerText = rate + '%';
    document.getElementById('average-score').innerText = avg;

    // 2. Render Bảng dữ liệu
    const tbody = document.getElementById('result-table-body');
    tbody.innerHTML = '';
    
    if (dataToRender.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" style="text-align:center; padding:20px;">Không có dữ liệu phù hợp</td></tr>';
    } else {
        dataToRender.forEach(item => {
            const statusClass = item.score >= 5.0 ? 'pass' : 'fail';
            const statusText = item.score >= 5.0 ? 'Hoàn thành' : 'Chưa đạt';
            
            const row = `
                <tr>
                    <td><strong>${item.studentId}</strong></td>
                    <td>${item.name}</td>
                    <td>${item.exam}</td>
                    <td>${item.date}</td>
                    <td><strong>${item.score.toFixed(1)}</strong></td>
                    <td><span class="badge ${statusClass}">${statusText}</span></td>
                </tr>
            `;
            tbody.insertAdjacentHTML('beforeend', row);
        });
    }

    // 3. Render Biểu đồ bằng Chart.js
    renderChart(dist);
}

// Hàm vẽ biểu đồ
function renderChart(distData) {
    const ctx = document.getElementById('scoreChart').getContext('2d');
    
    // Nếu biểu đồ đã tồn tại, hủy nó đi trước khi vẽ lại để tránh lỗi đè lấp
    if (currentChart) {
        currentChart.destroy();
    }

    currentChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: ['Dưới 4', 'Từ 4 - 6', 'Từ 6 - 8', 'Từ 8 - 10'],
            datasets: [{
                label: 'Số lượng sinh viên',
                data: [distData['< 4'], distData['4 - 6'], distData['6 - 8'], distData['8 - 10']],
                backgroundColor: [
                    'rgba(244, 67, 54, 0.7)',  // Đỏ (Yếu)
                    'rgba(255, 152, 0, 0.7)',  // Cam (Trung bình)
                    'rgba(33, 150, 243, 0.7)', // Xanh dương (Khá)
                    'rgba(76, 175, 80, 0.7)'   // Xanh lá (Giỏi)
                ],
                borderColor: [
                    '#f44336', '#ff9800', '#2196f3', '#4caf50'
                ],
                borderWidth: 1,
                borderRadius: 4
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: { stepSize: 1 } // Đếm số lượng người nên dùng số nguyên
                }
            },
            plugins: {
                legend: { display: false } // Ẩn chú thích vì đã có tên trục
            }
        }
    });
}

// Hàm Lọc dữ liệu
function applyFilter() {
    const examFilter = document.getElementById('filter-exam').value;
    const startDate = document.getElementById('filter-date-start').value;
    const endDate = document.getElementById('filter-date-end').value;

    let filteredData = rawData.filter(item => {
        // Lọc theo môn
        let matchExam = (examFilter === 'all' || item.type === examFilter);
        
        // Lọc theo ngày (so sánh chuỗi chuẩn YYYY-MM-DD)
        let matchStartDate = !startDate || item.date >= startDate;
        let matchEndDate = !endDate || item.date <= endDate;

        return matchExam && matchStartDate && matchEndDate;
    });

    updateDashboard(filteredData);
}

// Giả lập tính năng Xuất báo cáo
function exportData(type) {
    if (type === 'Excel') {
        alert("Đang tạo file Excel. Báo cáo sẽ được tải xuống trong giây lát...");
    } else {
        alert("Đang trích xuất báo cáo PDF...");
    }
}

// Khởi tạo hiển thị ban đầu với toàn bộ dữ liệu
updateDashboard(rawData);