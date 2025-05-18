package DuongVanBao.event.controller;

import DuongVanBao.event.dto.request.LoginRequest;
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
        return ResponseEntity.ok(SuccessResponse.withMessage(
            authService.register(request),
            "Đăng ký thành công"
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<SuccessResponse<?>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(SuccessResponse.withMessage(
            authService.login(request),
            "Đăng nhập thành công"
        ));
    }
}
