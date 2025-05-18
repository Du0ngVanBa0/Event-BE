package DuongVanBao.event.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class DatVeResponse {
    private String maDatVe;
    private BigDecimal tongTien;
    private String trangThai;
    private LocalDateTime thoiGianHetHan;
    private String url;
    private Boolean hoatDong;
    private EventResponseInTicket suKien;
    private List<ChiTietVeResponse> chiTietVes;
    private List<ThanhToanResponse> thanhToans;
    private NguoiDungResponse khachHang;

    @Data
    public static class ChiTietVeResponse {
        private String maVe;
        private String trangThai;
        private LocalDateTime thoiGianKiemVe;
        private LoaiVeInfo loaiVe;
    }

    @Data
    public static class LoaiVeInfo {
        private String maLoaiVe;
        private String tenLoaiVe;
        private BigDecimal giaTien;
    }

    @Data
    public static class EventResponseInTicket {
        private String maSuKien;
        private String tieuDe;
        private String anhBia;
        private Date thoiGianBatDau;
        private Date thoiGianKetThuc;
        private Date ngayMoBanVe;
        private Date ngayDongBanVe;
        private EventResponse.DiaDiemResponse diaDiem;
        private boolean hoatDong;
        private NguoiDungResponse nguoiToChuc;
    }

    @Data
    public static class NguoiDungResponse {
        private String maNguoiDung;
        private String tenHienThi;
        private String email;
    }

    @Data
    public static class ThanhToanResponse {
        private String maThanhToan;
        private BigDecimal soTien;
        private String phuongThuc;
        private String trangThai;
        private LocalDateTime thoiGianThanhToan;
    }
}
