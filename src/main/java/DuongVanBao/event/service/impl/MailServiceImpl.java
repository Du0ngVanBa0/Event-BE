package DuongVanBao.event.service.impl;

import DuongVanBao.event.enums.OtpType;
import DuongVanBao.event.model.entity.*;
import DuongVanBao.event.service.MailService;
import DuongVanBao.event.service.OtpService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
    private final JavaMailSender javaMailSender;
    private final OtpService otpService;
    private static final Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);
    @Value("${spring.mail.username}")
    private String mailUsername;

    @Value("${MAIL_NAME}")
    private String mailName;

    private static final int OTP_EXPIRATION_MINUTES = 5;
    private static final int QR_CODE_SIZE = 100;

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

    @Transactional
    @Override
    public void sendConfirmPurchaseEmail(DatVe datVe, NguoiDung nguoiDung, ThanhToan thanhToan) {
        // Force initialization of all lazy-loaded collections and entities
        // before the async operation starts
        Hibernate.initialize(datVe.getVes());
        Hibernate.initialize(nguoiDung);
        Hibernate.initialize(thanhToan);
        Hibernate.initialize(datVe);
        for (Ve ve : datVe.getVes()) {
            Hibernate.initialize(ve.getLoaiVe());
            if (ve.getLoaiVe() != null) {
                Hibernate.initialize(ve.getLoaiVe());
                Hibernate.initialize(ve.getLoaiVe().getSuKien());
                Hibernate.initialize(ve.getLoaiVe().getSuKien().getDiaDiem());
                Hibernate.initialize(ve.getLoaiVe().getKhuVuc());
            }
        }

        CompletableFuture.runAsync(() -> {
            try {
                MimeMessage message = javaMailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

                helper.setFrom(mailUsername, mailName);
                helper.setTo(nguoiDung.getEmail());
                helper.setSubject("Xác nhận thanh toán thành công - Universe Event");

                Map<String, byte[]> qrCodes = new HashMap<>();
                helper.setText(generatePurchaseConfirmationEmail(nguoiDung, datVe, thanhToan, qrCodes), true);

                for (Map.Entry<String, byte[]> entry : qrCodes.entrySet()) {
                    String qrId = entry.getKey();
                    byte[] qrCodeImage = entry.getValue();

                    if (qrCodeImage.length > 0) {
                        ByteArrayDataSource dataSource = new ByteArrayDataSource(qrCodeImage, "image/png");
                        helper.addInline(qrId, dataSource);
                    }
                }

                javaMailSender.send(message);
                logger.info("Confirmation email successfully sent to {}", nguoiDung.getEmail());
            } catch (Exception e) {
                logger.error("Error sending confirmation email to {}: {}", nguoiDung.getEmail(), e.getMessage(), e);            }
        });
    }

    private String generateSubject(OtpType otpType) {
        return switch (otpType) {
            case DANG_KY -> "Xác thực tài khoản - Universe Event";
            case DANG_NHAP -> "Mã OTP đăng nhập - Universe Event";
            case QUEN_MAT_KHAU -> "Đặt lại mật khẩu - Universe Event";
        };
    }

    private String generatePurchaseConfirmationEmail(NguoiDung nguoiDung, DatVe datVe, ThanhToan thanhToan, Map<String, byte[]> qrCodes) {
        String displayName = (nguoiDung.getTenHienThi() != null ? nguoiDung.getTenHienThi() : "Quý khách");

        Ve firstVe = datVe.getVes().iterator().next();
        SuKien suKien = firstVe.getLoaiVe().getSuKien();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        StringBuilder ticketRows = new StringBuilder();
        Map<String, Integer> ticketCounts = new HashMap<>();
        int ticketCounter = 0;
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        for (Ve ve : datVe.getVes()) {
            LoaiVe loaiVe = ve.getLoaiVe();
            String qrId = ve.getMaVe();
            String key = loaiVe.getMaLoaiVe();

            byte[] qrCodeImage = generateQRCodeImage(ve.getMaVe());
            qrCodes.put(qrId, qrCodeImage);
            ticketCounts.put(key, ticketCounts.getOrDefault(key, 0) + 1);

            ticketRows.append("<tr>")
                    .append("<td style=\"padding:8px;border:1px solid #ddd;\">").append(loaiVe.getTenLoaiVe()).append("</td>")
                    .append("<td style=\"padding:8px;border:1px solid #ddd;\">").append(loaiVe.getKhuVuc().getTenHienThi()).append("</td>")
                    .append("<td style=\"padding:8px;border:1px solid #ddd;\">").append(ve.getMaVe()).append("</td>")
                    .append("<td style=\"padding:8px;border:1px solid #ddd;text-align:center;\"><img src=\"cid:").append(qrId).append("\" width=\"" + QR_CODE_SIZE + "\" height=\""+ QR_CODE_SIZE +"\"></td>")
                    .append("<td style=\"padding:8px;border:1px solid #ddd;\">").append(currencyFormat.format(loaiVe.getGiaTien())).append("</td>")
                    .append("</tr>");

            ticketCounter++;
        }

        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "    <meta charset=\"UTF-8\">" +
                "    <title>Xác nhận thanh toán thành công</title>" +
                "    <style>" +
                "        body { font-family: 'Arial', sans-serif; color: #333; margin: 0; padding: 0; }" +
                "        .container { max-width: 600px; margin: 0 auto; }" +
                "        .header { background: linear-gradient(135deg, #1a237e, #4a148c); padding: 20px; text-align: center; border-radius: 8px 8px 0 0; }" +
                "        .header h1 { margin: 0; color: #ffffff; font-size: 24px; }" +
                "        .content { background: #f9f9f9; padding: 20px; border-radius: 0 0 8px 8px; }" +
                "        .event-info { background: white; padding: 15px; border-radius: 8px; margin: 15px 0; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }" +
                "        .event-title { color: #4a148c; margin-top: 0; }" +
                "        .ticket-table { width: 100%; border-collapse: collapse; margin: 15px 0; }" +
                "        .ticket-table th { background: #4a148c; color: white; padding: 8px; border: 1px solid #ddd; }" +
                "        .total { font-weight: bold; text-align: right; font-size: 16px; margin: 15px 0; }" +
                "        .footer { margin-top: 20px; text-align: center; font-size: 12px; color: #666; }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class=\"container\">" +
                "        <div class=\"header\">" +
                "            <h1>UNIVERSE EVENT</h1>" +
                "        </div>" +
                "        <div class=\"content\">" +
                "            <p>Xin chào <strong>" + displayName + "</strong>,</p>" +
                "            <p>Cảm ơn bạn đã sử dụng dịch vụ của Universe Event. Chúng tôi xác nhận rằng thanh toán của bạn đã được xử lý thành công.</p>" +
                "            " +
                "            <div class=\"event-info\">" +
                "                <h2 class=\"event-title\">" + suKien.getTieuDe() + "</h2>" +
                "                <p><strong>Thời gian:</strong> " + suKien.getThoiGianBatDau().format(formatter) + " - " + suKien.getThoiGianKetThuc().format(formatter) + "</p>" +
                "                <p><strong>Địa điểm:</strong> " + suKien.getDiaDiem().getTenDiaDiem() + "</p>" +
                "            </div>" +
                "            " +
                "            <h3>Thông tin đặt vé</h3>" +
                "            <p><strong>Mã đơn hàng:</strong> " + datVe.getMaDatVe() + "</p>" +
                "            <p><strong>Ngày thanh toán:</strong> " + thanhToan.getThoiGianThanhToan().format(formatter) + "</p>" +
                "            <p><strong>Phương thức thanh toán:</strong> " + thanhToan.getPhuongThuc() + "</p>" +
                "            <p><strong>Số lượng vé:</strong> " + ticketCounter + "</p>" +
                "            " +
                "            <h3>Chi tiết vé</h3>" +
                "            <table class=\"ticket-table\">" +
                "                <thead>" +
                "                    <tr>" +
                "                        <th>Loại vé</th>" +
                "                        <th>Khu vực</th>" +
                "                        <th>Mã vé</th>" +
                "                        <th>QR Code</th>" +
                "                        <th>Giá</th>" +
                "                    </tr>" +
                "                </thead>" +
                "                <tbody>" +
                ticketRows +
                "                </tbody>" +
                "            </table>" +
                "            " +
                "            <div class=\"total\">" +
                "                Tổng thanh toán: " + currencyFormat.format(datVe.getTongTien()) +
                "            </div>" +
                "            " +
                "            <p>Bạn có thể kiểm tra và quản lý vé của mình bằng cách đăng nhập vào tài khoản Universe Event.</p>" +
                "            <p>Vui lòng xuất trình vé này (dạng điện tử hoặc in) khi tham dự sự kiện.</p>" +
                "            " +
                "            <div class=\"footer\">" +
                "                <p>© 2024 Universe Event. Tất cả các quyền được bảo lưu.</p>" +
                "                <p>Đây là email tự động, vui lòng không trả lời.</p>" +
                "            </div>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";
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

    private byte[] generateQRCodeImage(String text) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, MailServiceImpl.QR_CODE_SIZE, MailServiceImpl.QR_CODE_SIZE);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            logger.error("Error generating QR code: {}", e.getMessage(), e);
            return new byte[0];
        }
    }
}