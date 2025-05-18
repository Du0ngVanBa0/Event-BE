package DuongVanBao.event.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {
    private long tongSuKien;
    private long tongSuKienChoDuyet;
    private long tongNguoiDung;
    private BigDecimal doanhThuThang;
    private List<DanhMucPhoBien> danhMucPhoBien;
    private List<SuKienHot> suKienHot;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DanhMucPhoBien {
        private String tenDanhMuc;
        private long soSuKien;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SuKienHot {
        private String tenSuKien;
        private String maSuKien;
        private long soVeBan;
        private BigDecimal doanhThu;
    }
}