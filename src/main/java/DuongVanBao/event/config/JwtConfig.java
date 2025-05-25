package DuongVanBao.event.config;

import DuongVanBao.event.model.entity.NguoiDung;
import DuongVanBao.event.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtConfig {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    private Key getSignKey() {
        return key;
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractMaNguoiDung(String token) {
        return extractClaim(token, claims -> claims.get("maNguoiDung", String.class));
    }

    public String extractTenNguoiDung(String token) {
        return extractClaim(token, claims -> claims.get("tenNguoiDung", String.class));
    }

    public String extractTenHienThi(String token) {
        return extractClaim(token, claims -> claims.get("tenHienThi", String.class));
    }

    public Role extractVaiTro(String token) {
        return Role.valueOf(extractClaim(token, claims -> claims.get("vaiTro", String.class)));
    }

    public String extractAnhDaiDien(String token) {
        return extractClaim(token, claims -> claims.get("anhDaiDien", String.class));
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof NguoiDung nguoiDung) {
            claims.put("maNguoiDung", nguoiDung.getMaNguoiDung());
            claims.put("email", nguoiDung.getEmail());
            claims.put("tenNguoiDung", nguoiDung.getTenNguoiDung());
            claims.put("tenHienThi", nguoiDung.getTenHienThi());
            claims.put("vaiTro", nguoiDung.getVaiTro().name());
            claims.put("anhDaiDien", nguoiDung.getAnhDaiDien());
        }
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String maNguoiDung = extractMaNguoiDung(token);
        return (maNguoiDung.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}