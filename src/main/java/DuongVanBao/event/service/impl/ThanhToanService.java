package DuongVanBao.event.service.impl;

import DuongVanBao.event.config.VNPayConfig;
import DuongVanBao.event.dto.request.CreatePaymentRequest;
import DuongVanBao.event.dto.response.CreatePaymentResponse;
import DuongVanBao.event.dto.response.PaymentResultResponse;
import DuongVanBao.event.model.entity.DatVe;
import DuongVanBao.event.model.entity.ThanhToan;
import DuongVanBao.event.model.entity.Ve;
import DuongVanBao.event.repository.DatVeRepository;
import DuongVanBao.event.repository.ThanhToanRepository;
import DuongVanBao.event.repository.VeRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ThanhToanService {
    private final VNPayConfig vnPayConfig;
    private final DatVeRepository datVeRepository;
    private final ThanhToanRepository thanhToanRepository;
    private final VeRepository veRepository;

    public CreatePaymentResponse createPaymentUrl(CreatePaymentRequest request, HttpServletRequest servletRequest) throws UnsupportedEncodingException {
        DatVe datVe = datVeRepository.findById(request.getMaDatVe())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đặt vé"));

        if (!"CHO_THANH_TOAN".equals(datVe.getTrangThai())) {
            throw new RuntimeException("Đơn đặt vé không ở trạng thái chờ thanh toán");
        }

        if (LocalDateTime.now().isAfter(datVe.getThoiGianHetHan())) {
            throw new RuntimeException("Đơn đặt vé đã hết hạn");
        }

        String vnp_TxnRef = datVe.getMaDatVe() + "-" + System.currentTimeMillis();
        String vnp_IpAddr = getClientIp(servletRequest);

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnPayConfig.getVersion());
        vnp_Params.put("vnp_Command", vnPayConfig.getCommand());
        vnp_Params.put("vnp_TmnCode", vnPayConfig.getTmnCode());
        vnp_Params.put("vnp_Amount", String.valueOf(datVe.getTongTien().multiply(new java.math.BigDecimal(100)).intValue()));
        vnp_Params.put("vnp_CurrCode", vnPayConfig.getCurrCode());
        vnp_Params.put("vnp_BankCode", request.getBankCode());
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan ve su kien: " + datVe.getMaDatVe());
        vnp_Params.put("vnp_OrderType", "250000");
        vnp_Params.put("vnp_Locale", vnPayConfig.getLocale());
        vnp_Params.put("vnp_ReturnUrl", vnPayConfig.getReturnUrl());
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        // Tạo chữ ký
        String queryUrl = query.toString();
        String vnp_SecureHash = hmacSHA512(vnPayConfig.getHashSecret(), hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = vnPayConfig.getPaymentUrl() + "?" + queryUrl;

        ThanhToan thanhToan = new ThanhToan();
        thanhToan.setDatVe(datVe);
        thanhToan.setSoTien(datVe.getTongTien());
        thanhToan.setPhuongThuc("VNPAY");
        thanhToan.setTrangThai("CHO_XU_LY");
        thanhToan.setThoiGianThanhToan(LocalDateTime.now());
        thanhToanRepository.save(thanhToan);

        datVe.setUrl(paymentUrl);
        datVeRepository.save(datVe);

        CreatePaymentResponse response = new CreatePaymentResponse();
        response.setPaymentUrl(paymentUrl);
        response.setMaThanhToan(thanhToan.getMaThanhToan());
        response.setMaDatVe(datVe.getMaDatVe());

        return response;
    }

    @Transactional
    public PaymentResultResponse processPaymentReturn(Map<String, String> vnpParams) {
        Map<String, String> parsedParams = parseVNPayParams(vnpParams);
        String vnp_ResponseCode = parsedParams.get("vnp_ResponseCode");
        String vnp_TxnRef = parsedParams.get("vnp_TxnRef");
        String vnp_TransactionNo = parsedParams.get("vnp_TransactionNo");
        String orderInfo = URLDecoder.decode(parsedParams.get("vnp_OrderInfo"), StandardCharsets.UTF_8);
        String payDate = parsedParams.get("vnp_PayDate");

        String vnp_Amount = parsedParams.get("vnp_Amount");
        String vnp_SecureHash = parsedParams.get("vnp_SecureHash");

        if (!verifySignature(parsedParams, vnp_SecureHash)) {
            throw new RuntimeException("Sai thông tin");
        }

        // Extract maDatVe from vnp_TxnRef
        String maDatVe = vnp_TxnRef.substring(0, vnp_TxnRef.lastIndexOf('-'));

        DatVe datVe = datVeRepository.findById(maDatVe)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đặt vé"));

        ThanhToan thanhToan = thanhToanRepository.findLatestByDatVeAndPhuongThuc(datVe, "VNPAY")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin thanh toán"));

        if (Objects.equals(thanhToan.getTrangThai(), "THANH_CONG")) {
            throw new RuntimeException("Mã đặt vé đã được thanh toán");
        }

        // Convert params to JSON string
        String paramsJson = new JSONObject(parsedParams).toString();
        thanhToan.setPhanHoiVNP(paramsJson);

        PaymentResultResponse response = new PaymentResultResponse();
        response.setMaDatVe(datVe.getMaDatVe());
        response.setMaGiaoDich(vnp_TransactionNo);
        response.setMaThanhToan(thanhToan.getMaThanhToan());
        response.setTongTien(Long.parseLong(vnp_Amount)/100);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        response.setNgayThanhToan(LocalDateTime.parse(payDate, formatter));

        if ("00".equals(vnp_ResponseCode)) {
            // Payment successful
            datVe.setTrangThai("DA_THANH_TOAN");
            datVe.setHoatDong(true);
            datVeRepository.save(datVe);

            List<Ve> ves = veRepository.findByDatVe(datVe);
            for (Ve ve : ves) {
                ve.setTrangThai("DA_THANH_TOAN");
                veRepository.save(ve);
            }

            thanhToan.setTrangThai("THANH_CONG");
            thanhToan.setPhanHoiVNP(vnp_TransactionNo + '-' + orderInfo);
            thanhToanRepository.save(thanhToan);

            response.setSuccess(true);
            response.setMessage("Thanh toán thành công");
        } else {
            // Payment failed
            thanhToan.setTrangThai("THAT_BAI");
            thanhToanRepository.save(thanhToan);

            response.setSuccess(false);
            response.setMessage("Thanh toán thất bại: " + getResponseDescription(vnp_ResponseCode));
        }

        return response;
    }

    public void deleteByDatVe(DatVe datVe) {
        thanhToanRepository.deleteByDatVe(datVe);
    }

    public static Map<String, String> parseVNPayParams(Map<String, String> rawParams) {
        Map<String, String> parsedParams = new HashMap<>();

        for (String entry : rawParams.values()) {
            int equalIndex = entry.indexOf('=');
            if (equalIndex > 0) {
                String key = entry.substring(0, equalIndex);
                String value = entry.substring(equalIndex + 1);
                parsedParams.put(key, value);
            }
        }

        return parsedParams;
    }

    private String getResponseDescription(String responseCode) {
        Map<String, String> responseDescriptions = new HashMap<>();
        responseDescriptions.put("00", "Giao dịch thành công");
        responseDescriptions.put("01", "Giao dịch đã tồn tại");
        responseDescriptions.put("02", "Merchant không hợp lệ");
        responseDescriptions.put("03", "Dữ liệu gửi sang không đúng định dạng");
        responseDescriptions.put("04", "Khởi tạo GD không thành công do Website đang bị tạm khóa");
        responseDescriptions.put("05", "Giao dịch không thành công do: Quý khách nhập sai mật khẩu thanh toán quá số lần quy định");
        responseDescriptions.put("06", "Giao dịch không thành công do Quý khách nhập sai mật khẩu xác thực giao dịch");
        responseDescriptions.put("07", "Giao dịch bị nghi ngờ là giao dịch gian lận");
        responseDescriptions.put("09", "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng bị khóa");
        responseDescriptions.put("10", "Giao dịch không thành công do: Thẻ hết hạn");
        responseDescriptions.put("11", "Giao dịch không thành công do: Thẻ bị từ chối");
        responseDescriptions.put("12", "Giao dịch không thành công do: Thẻ chưa đăng ký dịch vụ InternetBanking");
        responseDescriptions.put("13", "Giao dịch không thành công do: Quý khách nhập sai mật khẩu xác thực giao dịch");
        responseDescriptions.put("24", "Giao dịch không thành công do: Khách hàng hủy giao dịch");
        responseDescriptions.put("51", "Giao dịch không thành công do: Tài khoản không đủ số dư");
        responseDescriptions.put("65", "Giao dịch không thành công do: Tài khoản quý khách đã vượt quá hạn mức giao dịch trong ngày");
        responseDescriptions.put("75", "Ngân hàng thanh toán đang bảo trì");
        responseDescriptions.put("79", "Giao dịch không thành công do: KH nhập sai mật khẩu thanh toán quá số lần quy định");
        responseDescriptions.put("99", "Các lỗi khác");

        return responseDescriptions.getOrDefault(responseCode, "Lỗi không xác định");
    }

    private boolean verifySignature(Map<String, String> vnpParams, String secureHash) {
        Map<String, String> params = new HashMap<>(vnpParams);
        params.remove("vnp_SecureHash");
        params.remove("vnp_SecureHashType");

        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        for (int i = 0; i < fieldNames.size(); i++) {
            String fieldName = fieldNames.get(i);
            String fieldValue = params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                hashData.append(fieldName).append("=").append(fieldValue);
                if (i < fieldNames.size() - 1) {
                    hashData.append("&");
                }
            }
        }

        String calculatedHash = hmacSHA512(vnPayConfig.getHashSecret(), hashData.toString());

        return calculatedHash.equals(secureHash);
    }

    private String hmacSHA512(String key, String data) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "HmacSHA512");
            hmac.init(secretKeySpec);
            byte[] result = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(result);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Error creating HMAC SHA512", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private String getClientIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }
}
