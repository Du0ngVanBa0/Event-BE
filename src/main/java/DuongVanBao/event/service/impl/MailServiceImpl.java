package DuongVanBao.event.service.impl;

import DuongVanBao.event.enums.OtpType;
import DuongVanBao.event.model.entity.NguoiDung;
import DuongVanBao.event.model.entity.Otp;
import DuongVanBao.event.service.MailService;
import DuongVanBao.event.service.OtpService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
    private final JavaMailSender javaMailSender;
    private final OtpService otpService;

    @Value("${spring.mail.username}")
    private String mailUsername;

    @Value("${MAIL_NAME}")
    private String mailName;

    private static final int OTP_EXPIRATION_MINUTES = 5;

    @Override
    public Otp sendOtpEmail(NguoiDung nguoiDung, OtpType otpType) {
        Otp otp = otpService.generateOtp(nguoiDung, otpType);

        CompletableFuture.runAsync(() -> {
            try {
                MimeMessage message = javaMailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

                helper.setFrom(mailUsername, mailName);
                helper.setTo(nguoiDung.getEmail());
                helper.setSubject(generateSubject(otpType));
                helper.setText(generateHtmlEmailContent(nguoiDung.getTenHienThi(), otp.getMaXacThuc(), otpType), true);

                javaMailSender.send(message);

                CompletableFuture.runAsync(() -> {
                });
            } catch (MessagingException | UnsupportedEncodingException e) {
                throw new RuntimeException("Không thể gửi email xác thực", e);
            }
        });
        return otp;
    }

    @Override
    public Otp resendOtpEmail(String maOtp) {
        Otp otp = otpService.resendOtp(maOtp);
        NguoiDung nguoiDung = otp.getNguoiDung();
        OtpType otpType = otp.getLoaiOtp();

        CompletableFuture.runAsync(() -> {
            try {
                MimeMessage message = javaMailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

                helper.setFrom(mailUsername, mailName);
                helper.setTo(nguoiDung.getEmail());
                helper.setSubject(generateSubject(otpType));
                helper.setText(generateHtmlEmailContent(nguoiDung.getTenHienThi(), otp.getMaXacThuc(), otpType), true);

                javaMailSender.send(message);
            } catch (Exception e) {
                throw new RuntimeException("Không thể gửi lại email xác thực", e);
            }
        });
        return otp;
    }

    private String generateSubject(OtpType otpType) {
        return switch (otpType) {
            case DANG_KY -> "Xác thực tài khoản - Universe Event";
            case DANG_NHAP -> "Mã OTP đăng nhập - Universe Event";
            case QUEN_MAT_KHAU -> "Đặt lại mật khẩu - Universe Event";
        };
    }

    private String generateHtmlEmailContent(String tenNguoiDung, String otp, OtpType otpType) {
        String displayName = (tenNguoiDung != null ? tenNguoiDung : "bạn");

        String purpose = switch (otpType) {
            case DANG_KY -> "Xác thực đăng ký tài khoản";
            case DANG_NHAP -> "Xác thực đăng nhập";
            case QUEN_MAT_KHAU -> "Đặt lại mật khẩu";
        };

        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "    <meta charset=\"UTF-8\">" +
                "    <title>Universe Event</title>" +
                "    <style>" +
                "        body {" +
                "            margin: 0;" +
                "            padding: 0;" +
                "            font-family: 'Arial', sans-serif;" +
                "            color: #ffffff;" +
                "        }" +
                "        .container {" +
                "            width: 100%;" +
                "            max-width: 600px;" +
                "            margin: 0 auto;" +
                "        }" +
                "        .header {" +
                "            background: linear-gradient(135deg, #1a237e, #4a148c);" +
                "            padding: 20px;" +
                "            text-align: center;" +
                "            border-top-left-radius: 8px;" +
                "            border-top-right-radius: 8px;" +
                "        }" +
                "        .header h1 {" +
                "            margin: 0;" +
                "            color: #ffffff;" +
                "            font-size: 24px;" +
                "        }" +
                "        .content {" +
                "            background-size: cover;" +
                "            background-position: center;" +
                "            padding: 30px;" +
                "            border-bottom-left-radius: 8px;" +
                "            border-bottom-right-radius: 8px;" +
                "            position: relative;" +
                "        }" +
                "        .content::before {" +
                "            content: \"\";" +
                "            position: absolute;" +
                "            top: 0;" +
                "            left: 0;" +
                "            right: 0;" +
                "            bottom: 0;" +
                "            background: rgba(8, 14, 44, 0.85);" +
                "            z-index: 0;" +
                "            border-bottom-left-radius: 8px;" +
                "            border-bottom-right-radius: 8px;" +
                "        }" +
                "        .content-inner {" +
                "            position: relative;" +
                "            z-index: 1;" +
                "        }" +
                "        .greeting {" +
                "            font-size: 18px;" +
                "            margin-bottom: 20px;" +
                "        }" +
                "        .otp-container {" +
                "            background: rgba(255, 255, 255, 0.1);" +
                "            border-radius: 8px;" +
                "            padding: 20px;" +
                "            margin: 20px 0;" +
                "            text-align: center;" +
                "            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);" +
                "            backdrop-filter: blur(4px);" +
                "            border: 1px solid rgba(255, 255, 255, 0.2);" +
                "        }" +
                "        .otp-code {" +
                "            font-size: 32px;" +
                "            letter-spacing: 5px;" +
                "            font-weight: bold;" +
                "            text-shadow: 0 0 10px rgba(255, 255, 255, 0.5);" +
                "        }" +
                "        .info {" +
                "            margin: 20px 0;" +
                "            line-height: 1.6;" +
                "        }" +
                "        .footer {" +
                "            margin-top: 30px;" +
                "            text-align: center;" +
                "            font-size: 12px;" +
                "        }" +
                "        .divider {" +
                "            border-top: 1px solid rgba(255, 255, 255, 0.2);" +
                "            margin: 20px 0;" +
                "        }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class=\"container\">" +
                "        <div class=\"header\">" +
                "            <h1>UNIVERSE EVENT</h1>" +
                "        </div>" +
                "        <div class=\"content\">" +
                "            <div class=\"content-inner\">" +
                "                <div class=\"greeting\">" +
                "                    Xin chào <strong>" + displayName + "</strong>," +
                "                </div>" +
                "                <div class=\"info\">" +
                "                    Chúng tôi đã nhận được yêu cầu <strong>" + purpose + "</strong> từ bạn. Vui lòng sử dụng mã OTP bên dưới để xác thực:" +
                "                </div>" +
                "                <div class=\"otp-container\">" +
                "                    <div class=\"otp-code\">" + otp + "</div>" +
                "                </div>" +
                "                <div class=\"info\">" +
                "                    Mã này sẽ hết hạn sau <strong>" + OTP_EXPIRATION_MINUTES + " phút</strong>. Vui lòng không chia sẻ mã này với bất kỳ ai." +
                "                </div>" +
                "                <div class=\"divider\"></div>" +
                "                <div class=\"info\">" +
                "                    Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email này hoặc liên hệ với chúng tôi để được hỗ trợ." +
                "                </div>" +
                "                <div class=\"footer\">" +
                "                    <div>© 2024 Universe Event. Tất cả các quyền được bảo lưu.</div>" +
                "                </div>" +
                "            </div>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }
}