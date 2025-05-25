package DuongVanBao.event.config;

import DuongVanBao.event.service.impl.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtConfig jwtConfig;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String maNguoiDung;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        try {
            maNguoiDung = jwtConfig.extractMaNguoiDung(jwt);
        } catch (Exception e) {
            filterChain.doFilter(request, response);
            return;
        }

        if (maNguoiDung != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(maNguoiDung);
            if (jwtConfig.isTokenValid(jwt, userDetails)) {
                Map<String, Object> additionalDetails = new HashMap<>();
                additionalDetails.put("maNguoiDung", jwtConfig.extractMaNguoiDung(jwt));
                additionalDetails.put("tenNguoiDung", jwtConfig.extractTenNguoiDung(jwt));
                additionalDetails.put("tenHienThi", jwtConfig.extractTenHienThi(jwt));
                additionalDetails.put("vaiTro", jwtConfig.extractVaiTro(jwt));
                additionalDetails.put("anhDaiDien", jwtConfig.extractAnhDaiDien(jwt));

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(additionalDetails);
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
