//package ltw.examsystem.security;
//
//import ltw.examsystem.security.jwt.AuthEntryPointJwt;
//import ltw.examsystem.security.jwt.AuthTokenFilter;
//import ltw.examsystem.security.services.UserDetailsServiceImpl;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//@Configuration
//@EnableMethodSecurity
//public class WebSecurityConfig {
//
//    @Autowired
//    UserDetailsServiceImpl userDetailsService;
//
//    @Autowired
//    private AuthEntryPointJwt unauthorizedHandler;
//
//    @Bean
//    public AuthTokenFilter authenticationJwtTokenFilter() {
//        return new AuthTokenFilter();
//    }
//
//    // Cấu hình Provider cung cấp dữ liệu User cho Spring Security
//    @Bean
//    public DaoAuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(userDetailsService);
//        authProvider.setPasswordEncoder(passwordEncoder()); // Khai báo cách giải mã mật khẩu
//        return authProvider;
//    }
//
//    // Bean này dùng để gọi hàm xác thực trong AuthController
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
//        return authConfig.getAuthenticationManager();
//    }
//
//    // Thuật toán mã hóa mật khẩu (BCrypt là chuẩn an toàn nhất hiện nay)
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    // Cấu hình phân quyền các API Endpoints
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.csrf(csrf -> csrf.disable()) // Tắt CSRF vì chúng ta dùng JWT (không dùng Cookie/Session)
//                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler)) // Xử lý khi lỗi 401 (chưa đăng nhập)
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Tắt Session
//                .authorizeHttpRequests(auth ->
//                        auth.requestMatchers("/api/auth/**").permitAll() // Cho phép TẤT CẢ truy cập API đăng nhập, đăng ký
//                                .requestMatchers("/api/admin/**").hasRole("ADMIN") // Chỉ có quyền ADMIN mới được vào các API quản lý
//                                .requestMatchers("/api/exams/**", "/api/submissions/**").hasAnyRole("USER", "ADMIN") // Sinh viên và Admin đều được thao tác với bài thi
//                                .anyRequest().authenticated() // Bất kỳ request nào khác đều phải có token hợp lệ
//                );
//
//        // Đăng ký Provider
//        http.authenticationProvider(authenticationProvider());
//
//        // Thêm cái Filter chặn Token (AuthTokenFilter) vào TRƯỚC lớp Filter mặc định của Spring
//        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//}