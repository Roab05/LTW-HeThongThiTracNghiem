//package ltw.examsystem.security.jwt;
//
//import ltw.examsystem.security.services.UserDetailsServiceImpl;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.util.StringUtils;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//public class AuthTokenFilter extends OncePerRequestFilter {
//
//    @Autowired
//    private JwtUtils jwtUtils;
//
//    @Autowired
//    private UserDetailsServiceImpl userDetailsService;
//
//    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//        try {
//            // 1. Lấy token từ request header
//            String jwt = parseJwt(request);
//
//            // 2. Nếu có token và token hợp lệ
//            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
//
//                // Lấy username từ chuỗi JWT
//                String username = jwtUtils.getUserNameFromJwtToken(jwt);
//
//                // Load thông tin chi tiết của user từ Database
//                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//
//                // Tạo đối tượng Authentication để báo cho Spring Security biết user này hợp lệ
//                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//                        userDetails, null, userDetails.getAuthorities());
//                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//
//                // Lưu thông tin Authentication vào SecurityContext
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//            }
//        } catch (Exception e) {
//            logger.error("Không thể thiết lập xác thực người dùng: {}", e.getMessage());
//        }
//
//        // 3. Cho phép request đi tiếp đến các Controller
//        filterChain.doFilter(request, response);
//    }
//
//    /**
//     * Hàm phụ trợ: Trích xuất chuỗi JWT từ header "Authorization"
//     * Format chuẩn của Header: "Authorization: Bearer <chuỗi_token>"
//     */
//    private String parseJwt(HttpServletRequest request) {
//        String headerAuth = request.getHeader("Authorization");
//
//        // Kiểm tra xem header có chứa chuỗi văn bản và có bắt đầu bằng "Bearer " hay không
//        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
//            // Cắt bỏ 7 ký tự đầu ("Bearer ") để lấy đúng chuỗi token
//            return headerAuth.substring(7);
//        }
//
//        return null;
//    }
//}