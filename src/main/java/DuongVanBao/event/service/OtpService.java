package DuongVanBao.event.service;

import DuongVanBao.event.enums.OtpType;
import DuongVanBao.event.model.entity.NguoiDung;
import DuongVanBao.event.model.entity.Otp;

import java.util.Optional;

public interface OtpService {
    boolean verifyOtp(String maOtp, String maXacThuc);
    Otp generateOtp(NguoiDung nguoiDung, OtpType otpType);
    Optional<Otp> findByMaOtp(String maOtp);
    Otp resendOtp(String maOtp);
}
