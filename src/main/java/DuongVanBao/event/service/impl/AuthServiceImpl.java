package DuongVanBao.event.service.impl;

import DuongVanBao.event.config.JwtConfig;
import DuongVanBao.event.dto.request.LoginRequest;
import DuongVanBao.event.dto.request.RegisterRequest;
import DuongVanBao.event.dto.response.AuthResponse;
import DuongVanBao.event.enums.Role;
import DuongVanBao.event.model.entity.NguoiDung;
import DuongVanBao.event.repository.NguoiDungRepository;
import DuongVanBao.event.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final NguoiDungRepository nguoiDungRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtConfig jwtConfig;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (nguoiDungRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        NguoiDung nguoiDung = new NguoiDung();
        nguoiDung.setEmail(request.getEmail());
        nguoiDung.setMatKhau(passwordEncoder.encode(request.getMatKhau()));
        nguoiDung.setTenNguoiDung(request.getTenNguoiDung());
        nguoiDung.setTenHienThi(request.getTenHienThi());
        nguoiDung.setNgayTao(LocalDateTime.now());
        nguoiDung.setVaiTro(Role.USER);

        var savedUser = nguoiDungRepository.save(nguoiDung);
        var token = jwtConfig.generateToken(savedUser);
        
        return AuthResponse.builder()
                .token(token)
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getMatKhau()));
            
            var user = nguoiDungRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Email không tồn tại"));
            
            var token = jwtConfig.generateToken(user);
            
            return AuthResponse.builder()
                    .token(token)
                    .build();
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Thông tin đăng nhập không chính xác");
        }
    }
}
