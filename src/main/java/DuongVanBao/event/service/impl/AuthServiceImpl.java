package DuongVanBao.event.service.impl;

import DuongVanBao.event.config.JwtConfig;
import DuongVanBao.event.dto.request.LoginRequest;
import DuongVanBao.event.dto.request.OtpVerifyRequest;
import DuongVanBao.event.dto.request.RegisterRequest;
import DuongVanBao.event.dto.response.AuthResponse;
import DuongVanBao.event.enums.OtpType;
import DuongVanBao.event.enums.Role;
import DuongVanBao.event.model.entity.NguoiDung;
import DuongVanBao.event.model.entity.Otp;
import DuongVanBao.event.repository.NguoiDungRepository;
import DuongVanBao.event.service.AuthService;
import DuongVanBao.event.service.MailService;
import DuongVanBao.event.service.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final NguoiDungRepository nguoiDungRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtConfig jwtConfig;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;
    private final OtpService otpService;

    @Override
    public AuthResponse register(RegisterRequest request) {
        Optional<NguoiDung> existingActiveUser = nguoiDungRepository.findByEmailAndHoatDong(request.getEmail(), true);
        if (existingActiveUser.isPresent()) {
            throw new RuntimeException("Email đã tồn tại");
        }

        NguoiDung nguoiDung = new NguoiDung();
        nguoiDung.setEmail(request.getEmail());
        nguoiDung.setMatKhau(passwordEncoder.encode(request.getMatKhau()));
        nguoiDung.setTenNguoiDung(request.getTenNguoiDung());
        nguoiDung.setTenHienThi(request.getTenHienThi());
        nguoiDung.setNgayTao(LocalDateTime.now());
        nguoiDung.setVaiTro(Role.USER);
        nguoiDung.setHoatDong(false);

        nguoiDungRepository.save(nguoiDung);
        Otp otp = mailService.sendOtpEmail(nguoiDung, OtpType.DANG_KY);
        
        return AuthResponse.builder()
                .maOtp(otp.getMaOtp())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            var user = nguoiDungRepository.findByEmailAndHoatDong(request.getEmail(), true)
                    .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getMaNguoiDung(), request.getMatKhau()));

            var token = jwtConfig.generateToken(user);
            
            return AuthResponse.builder()
                    .token(token)
                    .build();
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Thông tin đăng nhập không chính xác");
        }
    }

    @Override
    public AuthResponse verifyRegisterOtp(OtpVerifyRequest request) {
        boolean verified = otpService.verifyOtp(request.getMaOtp(), request.getMaXacThuc());

        if (!verified) {
            throw new RuntimeException("Xác thực không thành công");
        }

        Otp otp = otpService.findByMaOtp(request.getMaOtp())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mã OTP"));

        NguoiDung nguoiDung = otp.getNguoiDung();
        nguoiDung.setHoatDong(true);
        nguoiDung.setNgayTao(LocalDateTime.now());
        nguoiDungRepository.save(nguoiDung);

        var token = jwtConfig.generateToken(nguoiDung);

        return AuthResponse.builder()
                .token(token)
                .build();
    }

    @Override
    public AuthResponse resendOtp(String maOtp) {
        Otp otp = mailService.resendOtpEmail(maOtp);

        return AuthResponse.builder()
                .maOtp(otp.getMaOtp())
                .build();
    }
}
