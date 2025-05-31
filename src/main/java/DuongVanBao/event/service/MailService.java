package DuongVanBao.event.service;

import DuongVanBao.event.enums.OtpType;
import DuongVanBao.event.model.entity.DatVe;
import DuongVanBao.event.model.entity.NguoiDung;
import DuongVanBao.event.model.entity.Otp;
import DuongVanBao.event.model.entity.ThanhToan;

public interface MailService {
    Otp sendOtpEmail(NguoiDung nguoiDung, OtpType otpType);
    Otp resendOtpEmail(String maOtp);
    void sendConfirmPurchaseEmail(DatVe datVe, NguoiDung nguoiDung, ThanhToan thanhToan);
}