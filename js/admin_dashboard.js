let examsData = [
    { id: 1, code: "230056PCNCT", name: "Kiểm tra cuối kì - Triết học", type: "Kiểm tra", status: "Sẵn sàng" },
    { id: 2, code: "230728PCNCT", name: "Kiểm tra giữa kì - C++", type: "Kiểm tra", status: "Đã hết hạn" },
    { id: 3, code: "230070PCNCT", name: "Luyện tập đồ thị", type: "Luyện tập", status: "Chưa bắt đầu" }
];

let usersData = [
    { id: 1, studentId: "B21DCCN001", name: "Nguyễn Văn A", class: "D21CQCN01-B", email: "nva@stu.ptit.edu.vn" },
    { id: 2, studentId: "B21DCCN002", name: "Trần Thị B", class: "D21CQCN02-B", email: "ttb@stu.ptit.edu.vn" },
    { id: 3, studentId: "B21DCCN003", name: "Lê Hoàng C", class: "D21CQCN01-B", email: "lhc@stu.ptit.edu.vn" }
];

function switchTab(tabId, element) {
    document.querySelectorAll('.nav-item').forEach(item => item.classList.remove('active'));
    element.classList.add('active');

    const titles = {
        'dashboard': 'Thống kê tổng quan',
        'exams': 'Quản lý kỳ thi',
        'users': 'Quản lý sinh viên'
    };
    document.getElementById('page-title').innerText = titles[tabId];

    document.querySelectorAll('.view-section').forEach(view => view.classList.remove('active'));
    document.getElementById('view-' + tabId).classList.add('active');
}

function renderExamsTable() {
    const tbody = document.getElementById('exam-table-body');
    tbody.innerHTML = '';
    examsData.forEach(exam => {
        let badgeClass = exam.status === 'Sẵn sàng' ? 'success' : (exam.status === 'Đã hết hạn' ? 'warning' : 'neutral');
        
        let row = `
            <tr>
                <td><strong>${exam.code}</strong></td>
                <td>${exam.name}</td>
                <td>${exam.type}</td>
                <td><span class="badge ${badgeClass}">${exam.status}</span></td>
                <td class="action-btns">
                    <button class="btn-edit" onclick="openModal('exam', ${exam.id})"><i class="fas fa-edit"></i></button>
                    <button class="btn-delete" onclick="deleteExam(${exam.id})"><i class="fas fa-trash-alt"></i></button>
                </td>
            </tr>
        `;
        tbody.insertAdjacentHTML('beforeend', row);
    });
}

function renderUsersTable() {
    const tbody = document.getElementById('user-table-body');
    tbody.innerHTML = '';
    usersData.forEach(user => {
        let row = `
            <tr>
                <td><strong>${user.studentId}</strong></td>
                <td>${user.name}</td>
                <td>${user.class}</td>
                <td>${user.email}</td>
                <td class="action-btns">
                    <button class="btn-view-result" onclick="window.location.href='admin_view_student_result.html?id=${user.studentId}'" style="color: #2e7d32;" title="Xem kết quả thi">
                        <i class="fas fa-eye"></i>
                    </button>
                    <button class="btn-edit" onclick="openModal('user', ${user.id})" title="Chỉnh sửa"><i class="fas fa-edit"></i></button>
                    <button class="btn-delete" onclick="deleteUser(${user.id})" title="Xóa"><i class="fas fa-trash-alt"></i></button>
                </td>
            </tr>
        `;
        tbody.insertAdjacentHTML('beforeend', row);
    });
}

function deleteExam(id) {
    if(confirm("Bạn có chắc chắn muốn xóa kỳ thi này?")) {
        examsData = examsData.filter(e => e.id !== id);
        renderExamsTable();
    }
}

function deleteUser(id) {
    if(confirm("Bạn có chắc chắn muốn xóa sinh viên này?")) {
        usersData = usersData.filter(u => u.id !== id);
        renderUsersTable();
    }
}

function openModal(type, id = null) {
    if (type === 'exam') {
        if (id) {
            window.location.href = `exam_update.html?id=${id}`;
        } else {
            window.location.href = 'exam_update.html';
        }
    } else {
        const modal = document.getElementById('admin-modal');
        const title = document.getElementById('modal-title');
        
        title.innerText = id ? 'Chỉnh sửa Sinh viên' : 'Thêm mới Sinh viên';
        modal.classList.add('active');
    }
}

function closeModal() {
    document.getElementById('admin-modal').classList.remove('active');
}

function saveData() {
    alert("Cập nhật dữ liệu thành công!");
    closeModal();
}

renderExamsTable();
renderUsersTable();