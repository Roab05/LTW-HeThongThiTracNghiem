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

function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

function handleLogin() {
    const usernameInput = document.getElementById('login-username').value.trim();
    const passwordInput = document.getElementById('login-password').value.trim();

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

function handleRegister() {
    const usernameInput = document.getElementById('reg-username').value.trim();
    const emailInput = document.getElementById('reg-email').value.trim();
    const passwordInput = document.getElementById('reg-password').value.trim();
    const confirmPasswordInput = document.getElementById('reg-confirm-password').value.trim();

    if (!usernameInput || !emailInput || !passwordInput || !confirmPasswordInput) {
        alert("Vui lòng điền đầy đủ thông tin đăng ký!");
        return;
    }

    if (!isValidEmail(emailInput)) {
        alert("Định dạng email không hợp lệ!");
        return;
    }

    if (passwordInput.length < 6) {
        alert("Mật khẩu phải có ít nhất 6 ký tự!");
        return;
    }

    if (passwordInput !== confirmPasswordInput) {
        alert("Xác nhận mật khẩu không khớp!");
        return;
    }

    alert(`Đăng ký tài khoản thành công!\nTên người dùng: ${usernameInput}\nEmail: ${emailInput}\n\nXin mời bạn đăng nhập.`);

    document.getElementById('reg-username').value = '';
    document.getElementById('reg-email').value = '';
    document.getElementById('reg-password').value = '';
    document.getElementById('reg-confirm-password').value = '';
    
    toggleForm('login');
}