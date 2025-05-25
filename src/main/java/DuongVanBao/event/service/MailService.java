package DuongVanBao.event.service;

import DuongVanBao.event.enums.OtpType;
import DuongVanBao.event.model.entity.NguoiDung;
import DuongVanBao.event.model.entity.Otp;

public interface MailService {
    Otp sendOtpEmail(NguoiDung nguoiDung, OtpType otpType);
    Otp resendOtpEmail(String maOtp);
}