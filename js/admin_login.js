// Hàm chuyển đổi giữa form Đăng nhập và Đăng ký
function toggleForm(formType) {
    const loginWrapper = document.getElementById('login-wrapper');
    const registerWrapper = document.getElementById('register-wrapper');

    if (formType === 'register') {
        loginWrapper.style.display = 'none';
        registerWrapper.style.display = 'flex';
    } else {
        loginWrapper.style.display = 'flex';
        registerWrapper.style.display = 'none';
    }
}

// Kiểm tra định dạng Email hợp lệ
function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

// Xử lý sự kiện Đăng nhập
function handleLogin() {
    const usernameInput = document.getElementById('login-username').value.trim();
    const passwordInput = document.getElementById('login-password').value.trim();

    // Validate
    if (!usernameInput) {
        alert("Vui lòng nhập Tên đăng nhập!");
        return;
    }
    if (!passwordInput) {
        alert("Vui lòng nhập Mật khẩu!");
        return;
    }

    window.location.href = 'admin_dashboard.html';
}

// Xử lý sự kiện Đăng ký
function handleRegister() {
    const usernameInput = document.getElementById('reg-username').value.trim();
    const emailInput = document.getElementById('reg-email').value.trim();
    const passwordInput = document.getElementById('reg-password').value.trim();
    const confirmPasswordInput = document.getElementById('reg-confirm-password').value.trim();

    // Validate các trường trống
    if (!usernameInput || !emailInput || !passwordInput || !confirmPasswordInput) {
        alert("Vui lòng điền đầy đủ thông tin đăng ký!");
        return;
    }

    // Validate Email
    if (!isValidEmail(emailInput)) {
        alert("Định dạng email không hợp lệ!");
        return;
    }

    // Validate độ dài mật khẩu (VD: tối thiểu 6 ký tự)
    if (passwordInput.length < 6) {
        alert("Mật khẩu phải có ít nhất 6 ký tự!");
        return;
    }

    // Validate mật khẩu khớp nhau
    if (passwordInput !== confirmPasswordInput) {
        alert("Xác nhận mật khẩu không khớp!");
        return;
    }

    // Demo thành công
    alert(`Đăng ký tài khoản thành công!\nTên người dùng: ${usernameInput}\nEmail: ${emailInput}\n\nXin mời bạn đăng nhập.`);
    
    // Tự động chuyển về form đăng nhập sau khi đăng ký thành công
    // Xóa trắng form đăng ký
    document.getElementById('reg-username').value = '';
    document.getElementById('reg-email').value = '';
    document.getElementById('reg-password').value = '';
    document.getElementById('reg-confirm-password').value = '';
    
    toggleForm('login');
}