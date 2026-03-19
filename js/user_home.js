const examsData = [
    { code: "230756PCNCT", name: "Luyện tập quay lui", time: "Không giới hạn", type: "Luyện tập", duration: "90 phút", status: "Sẵn sàng" },
    { code: "230056PCNCT", name: "Kiểm tra cuối kì", time: "8:30 25/02/2026", type: "Kiểm tra", duration: "90 phút", status: "Chưa bắt đầu" },
    { code: "230728PCNCT", name: "Kiểm tra giữa kì", time: "9:00 22/12/2025", type: "Kiểm tra", duration: "90 phút", status: "Đã hết hạn" },
    { code: "230070PCNCT", name: "Luyện tập đồ thị", time: "Không giới hạn", type: "Luyện tập", duration: "90 phút", status: "Sẵn sàng" }
];

const examListContainer = document.getElementById('examList');
const searchInput = document.getElementById('searchInput');
const statusFilter = document.getElementById('statusFilter');
const typeFilter = document.getElementById('typeFilter');

function renderExams(data) {
    examListContainer.innerHTML = '';
    
    if(data.length === 0) {
        examListContainer.innerHTML = '<p style="text-align:center; padding: 20px;">Không tìm thấy bài thi nào phù hợp.</p>';
        return;
    }

    data.forEach(exam => {
        const isExpired = exam.status === "Đã hết hạn";
        const btnClass = isExpired ? "btn-action btn-expired" : "btn-action";
        const btnText = isExpired ? "Quá hạn" : "Bắt đầu";
        const rowClass = isExpired ? "exam-row expired" : "exam-row";

        const rowHTML = `
            <div class="${rowClass}">
                <div class="exam-code">${exam.code}</div>
                <div class="exam-name">${exam.name}</div>
                <div class="exam-time">${exam.time}</div>
                <div class="exam-type">${exam.type}</div>
                <div class="exam-duration">${exam.duration}</div>
                <div class="exam-action">
                    <button class="${btnClass}" onclick="startExam('${exam.code}', ${isExpired})">${btnText}</button>
                </div>
            </div>
        `;
        examListContainer.insertAdjacentHTML('beforeend', rowHTML);
    });
}

function filterExams() {
    const keyword = searchInput.value.toLowerCase();
    const statusVal = statusFilter.value;
    const typeVal = typeFilter.value;

    const filteredData = examsData.filter(exam => {
        const matchKeyword = exam.name.toLowerCase().includes(keyword) || exam.code.toLowerCase().includes(keyword);
        const matchStatus = statusVal === 'all' || exam.status === statusVal;
        const matchType = typeVal === 'all' || exam.type === typeVal;
        return matchKeyword && matchStatus && matchType;
    });

    renderExams(filteredData);
}

function startExam(code, isExpired) {
    if (isExpired) {
        alert("Kỳ thi này đã quá hạn!");
        return;
    }
    window.location.href = 'exam.html'; 
}

function handleLogout() {
    const confirmLogout = confirm("Bạn có chắc chắn muốn đăng xuất không?");
    
    if (confirmLogout) {
        window.location.href = 'user_login.html'; 
    }
}

searchInput.addEventListener('input', filterExams);
statusFilter.addEventListener('change', filterExams);
typeFilter.addEventListener('change', filterExams);

renderExams(examsData);