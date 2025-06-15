package DuongVanBao.event.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {
    private long tongSuKien;
    private long tongSuKienChoDuyet;
    private long tongNguoiDung;
    private BigDecimal doanhThuThang;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KhachHangMuaNhieu {
        private String maNguoiDung;
        private String tenHienThi;
        private String email;
        private String anhDaiDien;
        private long soVe;
        private BigDecimal tongTien;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DoanhThuSuKien {
        private String maSuKien;
        private String tieuDe;
        private LocalDateTime thoiGianBatDau;
        private LocalDateTime thoiGianKetThuc;
        private BigDecimal tongDoanhThu;
        private long soVeDaBan;
        private long soVeConLai;
    }
}