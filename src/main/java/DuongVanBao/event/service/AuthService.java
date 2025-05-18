package DuongVanBao.event.service;

import DuongVanBao.event.dto.request.LoginRequest;
import DuongVanBao.event.dto.request.RegisterRequest;
import DuongVanBao.event.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
