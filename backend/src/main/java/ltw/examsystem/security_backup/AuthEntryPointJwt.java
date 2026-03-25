//package ltw.examsystem.security_backup;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.MediaType;
//import org.springframework.security_backup.core.AuthenticationException;
//import org.springframework.security_backup.web.AuthenticationEntryPoint;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
//@Component
//public class AuthEntryPointJwt implements AuthenticationEntryPoint {
//
//    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);
//
//    @Override
//    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
//            throws IOException, ServletException {
//
//        logger.error("Lỗi Unauthorized (401): {}", authException.getMessage());
//
//        // Định dạng trả về là JSON
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//
//        // Tạo nội dung thông báo lỗi
//        final Map<String, Object> body = new HashMap<>();
//        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
//        body.put("error", "Unauthorized");
//        body.put("message", "Bạn chưa đăng nhập hoặc phiên làm việc đã hết hạn.");
//        body.put("path", request.getServletPath());
//
//        // Ghi dữ liệu ra response
//        final ObjectMapper mapper = new ObjectMapper();
//        mapper.writeValue(response.getOutputStream(), body);
//    }
//}