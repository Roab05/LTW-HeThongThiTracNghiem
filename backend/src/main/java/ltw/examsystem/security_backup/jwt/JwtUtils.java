//package ltw.examsystem.security_backup.jwt;
//
//import ltw.examsystem.security_backup.services.UserDetailsImpl;
//import io.jsonwebtoken.*;
//import io.jsonwebtoken.io.Decoders;
//import io.jsonwebtoken.security_backup.Keys;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security_backup.core.Authentication;
//import org.springframework.stereotype.Component;
//
//import java.security_backup.Key;
//import java.util.Date;
//
//@Component
//public class JwtUtils {
//    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
//
//    // Lấy chuỗi bí mật từ file cấu hình (application.yml hoặc application.properties)
//    @Value("${app.jwtSecret}")
//    private String jwtSecret;
//
//    // Lấy thời gian sống của token từ file cấu hình (tính bằng milliseconds)
//    @Value("${app.jwtExpirationMs}")
//    private int jwtExpirationMs;
//
//    /**
//     * Tạo chuỗi JWT từ thông tin người dùng đã đăng nhập thành công
//     */
//    public String generateJwtToken(Authentication authentication) {
//
//        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
//
//        return Jwts.builder()
//                .setSubject((userPrincipal.getUsername()))
//                .setIssuedAt(new Date())
//                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
//                .signWith(key(), SignatureAlgorithm.HS256)
//                .compact();
//    }
//
//    /**
//     * Chuyển đổi chuỗi bí mật dạng Base64 thành đối tượng Key để mã hóa/giải mã
//     */
//    private Key key() {
//        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
//    }
//
//    /**
//     * Lấy username (subject) từ chuỗi JWT token
//     */
//    public String getUserNameFromJwtToken(String token) {
//        return Jwts.parserBuilder().setSigningKey(key()).build()
//                .parseClaimsJws(token).getBody().getSubject();
//    }
//
//    /**
//     * Kiểm tra tính hợp lệ của token (có bị sửa đổi không, có hết hạn không...)
//     */
//    public boolean validateJwtToken(String authToken) {
//        try {
//            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
//            return true;
//        } catch (MalformedJwtException e) {
//            logger.error("Invalid JWT token: {}", e.getMessage());
//        } catch (ExpiredJwtException e) {
//            logger.error("JWT token is expired: {}", e.getMessage());
//        } catch (UnsupportedJwtException e) {
//            logger.error("JWT token is unsupported: {}", e.getMessage());
//        } catch (IllegalArgumentException e) {
//            logger.error("JWT claims string is empty: {}", e.getMessage());
//        }
//
//        return false;
//    }
//}
