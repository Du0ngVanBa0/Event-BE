package DuongVanBao.event.service.impl;

import DuongVanBao.event.enums.OtpType;
import DuongVanBao.event.model.entity.NguoiDung;
import DuongVanBao.event.model.entity.Otp;
import DuongVanBao.event.repository.OtpRepository;
import DuongVanBao.event.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {
    private final OtpRepository otpRepository;

    private static final int OTP_LENGTH = 8;
    private static final int OTP_EXPIRATION_MINUTES = 5;

    @Override
    @Transactional
    public boolean verifyOtp(String maOtp, String maXacThuc) {
        Otp otp = otpRepository.findByMaOtp(maOtp)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mã OTP"));

        if (otp.isExpired()) {
            throw new RuntimeException("Mã OTP đã hết hạn");
        }

        if (!otp.getMaXacThuc().equals(maXacThuc)) {
            throw new RuntimeException("Mã xác thực không đúng");
        }

        otp.setDaXacThuc(true);
        otp.setThoiGianXacThuc(LocalDateTime.now());
        otpRepository.save(otp);
        return true;
    }

    public Otp generateOtp(NguoiDung nguoiDung, OtpType otpType) {
        String otpCode = generateRandomOtp();
        LocalDateTime now = LocalDateTime.now();

        Otp otp = Otp.builder()
                .nguoiDung(nguoiDung)
                .maXacThuc(otpCode)
                .thoiGianTao(now)
                .thoiGianHetHan(now.plusMinutes(OTP_EXPIRATION_MINUTES))
                .loaiOtp(otpType)
                .daXacThuc(false)
                .build();

        return otpRepository.save(otp);
    }

    @Override
    public Optional<Otp> findByMaOtp(String maOtp) {
        return otpRepository.findByMaOtp(maOtp);
    }

    @Override
    public Otp resendOtp(String maOtp) {
        Otp existingOtp = otpRepository.findByMaOtp(maOtp)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mã OTP"));

        LocalDateTime now = LocalDateTime.now();

        if (existingOtp.isDaXacThuc()) {
            throw new RuntimeException("Mã OTP đã được xác thực, không thể gửi lại");
        }

        LocalDateTime lastSentTime = existingOtp.getThoiGianTao();
        if (lastSentTime.plusMinutes(1).isAfter(now)) {
            long secondsToWait = java.time.Duration.between(now, lastSentTime.plusMinutes(1)).getSeconds();
            throw new RuntimeException("Vui lòng đợi " + secondsToWait + " giây nữa để gửi lại OTP");
        }

        String newOtpCode = generateRandomOtp();
        existingOtp.setMaXacThuc(newOtpCode);
        existingOtp.setThoiGianTao(now);
        existingOtp.setThoiGianHetHan(now.plusMinutes(OTP_EXPIRATION_MINUTES));
        existingOtp.setDaXacThuc(false);
        existingOtp.setThoiGianXacThuc(null);

        return otpRepository.save(existingOtp);
    }

    private String generateRandomOtp() {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }

        return otp.toString();
    }
}
