package DuongVanBao.event.util;

import DuongVanBao.event.model.entity.NguoiDung;
import DuongVanBao.event.model.UserDetailsImpl;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    public static NguoiDung getCurrentUser() {
        return ((UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal())
                .getNguoiDung();
    }
    
    public static String getCurrentUserId() {
        return getCurrentUser().getMaNguoiDung();
    }

    public static boolean hasRole(String role) {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role));
    }
}