package DuongVanBao.event.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class VeResponse {
    private String maVe;
    private String trangThai;
    private LocalDateTime thoiGianKiemVe;
    private LoaiVeInfo loaiVe;
    private EventInfo suKien;
    private KhachHangInfo khachHang;
    private DatVeInfo datVe;

    @Data
    public static class LoaiVeInfo {
        private String maLoaiVe;
        private String tenLoaiVe;
        private String moTa;
        private BigDecimal giaTien;
        private KhuVucInfo khuVuc;
    }

    @Data
    public static class KhuVucInfo {
        private String maKhuVuc;
        private String tenKhuVuc;
    }

    @Data
    public static class EventInfo {
        private String maSuKien;
        private String tieuDe;
        private String anhBia;
        private LocalDateTime thoiGianBatDau;
        private LocalDateTime thoiGianKetThuc;
        private DiaDiemInfo diaDiem;
        private boolean hoatDong;
        private NguoiToChucInfo nguoiToChuc;
    }

    @Data
    public static class DiaDiemInfo {
        private String maDiaDiem;
        private String tenPhuongXa;
        private String tenQuanHuyen;
        private String tenTinhThanh;
    }

    @Data
    public static class NguoiToChucInfo {
        private String maNguoiDung;
        private String tenHienThi;
    }

    @Data
    public static class KhachHangInfo {
        private String maNguoiDung;
        private String tenHienThi;
        private String email;
    }

    @Data
    public static class DatVeInfo {
        private String maDatVe;
        private String trangThai;
        private BigDecimal tongTien;
        private LocalDateTime thoiGianHetHan;
        private Boolean hoatDong;
    }
}