package DuongVanBao.event.controller;

import DuongVanBao.event.dto.request.LoginRequest;
import DuongVanBao.event.dto.request.OtpVerifyRequest;
import DuongVanBao.event.dto.request.RegisterRequest;
import DuongVanBao.event.dto.response.SuccessResponse;
import DuongVanBao.event.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<SuccessResponse<?>> register(@Valid @RequestBody RegisterRequest request) {
        var response = authService.register(request);

        return ResponseEntity.ok(SuccessResponse.withMessage(
                response,
                "Đăng ký thành công, vui lòng xác thực tài khoản qua email"
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<SuccessResponse<?>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(SuccessResponse.withMessage(
            authService.login(request),
            "Đăng nhập thành công"
        ));
    }

    @PostMapping("/verify-register-otp")
    public ResponseEntity<SuccessResponse<?>> verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {
        var response = authService.verifyRegisterOtp(request);

        return ResponseEntity.ok(SuccessResponse.withMessage(
                response,
                "Xác thực tài khoản thành công"
        ));
    }

    @PostMapping("/resend-otp/{maOtp}")
    public ResponseEntity<SuccessResponse<?>> resendOtp(@PathVariable String maOtp) {
        var response = authService.resendOtp(maOtp);

        return ResponseEntity.ok(SuccessResponse.withMessage(
                response,
                "Đã gửi lại mã xác thực, vui lòng kiểm tra email của bạn"
        ));
    }
}
