// Chuyển đổi giữa tab Đăng nhập và Đăng ký
function switchTab(tab) {
    document.getElementById('btnLoginTab').classList.remove('active');
    document.getElementById('btnRegisterTab').classList.remove('active');
    document.getElementById('loginForm').classList.remove('active');
    document.getElementById('registerForm').classList.remove('active');

    if (tab === 'login') {
        document.getElementById('btnLoginTab').classList.add('active');
        document.getElementById('loginForm').classList.add('active');
    } else {
        document.getElementById('btnRegisterTab').classList.add('active');
        document.getElementById('registerForm').classList.add('active');
    }
}

// Hàm hỗ trợ hiển thị lỗi
function showError(elementId, message) {
    document.getElementById(elementId).innerText = message;
}

function clearErrors(formId) {
    const errors = document.querySelectorAll(`#${formId} .error-message`);
    errors.forEach(err => err.innerText = '');
}

// Xử lý Validate Form Đăng Nhập
document.getElementById('loginForm').addEventListener('submit', function (e) {
    e.preventDefault();
    clearErrors('loginForm');

    let isValid = true;
    const username = document.getElementById('loginUsername').value.trim();
    const password = document.getElementById('loginPassword').value.trim();

    if (!username) {
        showError('loginUsernameErr', 'Vui lòng nhập tên đăng nhập');
        isValid = false;
    }
    if (!password) {
        showError('loginPasswordErr', 'Vui lòng nhập mật khẩu');
        isValid = false;
    }

    if (isValid) {
        alert('Đăng nhập thành công! (Chuyển hướng sang Trang Chính...)');
        // Ở đây sau này bạn sẽ dùng window.location.href = 'dashboard.html'
    }
});

// Xử lý Validate Form Đăng Ký
document.getElementById('registerForm').addEventListener('submit', function (e) {
    e.preventDefault();
    clearErrors('registerForm');

    let isValid = true;
    const username = document.getElementById('regUsername').value.trim();
    const email = document.getElementById('regEmail').value.trim();
    const password = document.getElementById('regPassword').value.trim();
    const confirmPassword = document.getElementById('regConfirmPassword').value.trim();

    // Validate Username
    if (!username) {
        showError('regUsernameErr', 'Vui lòng nhập tên người dùng');
        isValid = false;
    }

    // Validate Email bằng Regex
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!email) {
        showError('regEmailErr', 'Vui lòng nhập email');
        isValid = false;
    } else if (!emailRegex.test(email)) {
        showError('regEmailErr', 'Email không đúng định dạng');
        isValid = false;
    }

    // Validate Password
    if (!password) {
        showError('regPasswordErr', 'Vui lòng nhập mật khẩu');
        isValid = false;
    } else if (password.length < 6) {
        showError('regPasswordErr', 'Mật khẩu phải có ít nhất 6 ký tự');
        isValid = false;
    }

    // Validate Confirm Password
    if (!confirmPassword) {
        showError('regConfirmPasswordErr', 'Vui lòng xác nhận mật khẩu');
        isValid = false;
    } else if (password !== confirmPassword) {
        showError('regConfirmPasswordErr', 'Mật khẩu xác nhận không khớp');
        isValid = false;
    }

    if (isValid) {
        alert('Đăng ký tài khoản thành công! Vui lòng đăng nhập.');
        switchTab('login');
        document.getElementById('registerForm').reset();
    }
});