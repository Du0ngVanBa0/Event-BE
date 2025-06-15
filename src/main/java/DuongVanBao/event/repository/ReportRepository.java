package DuongVanBao.event.repository;

import DuongVanBao.event.dto.response.ReportResponse;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReportRepository{
    long countTongSuKien();
    long countSuKienChoDuyet();
    long countTongNguoiDung();

    BigDecimal calculateDoanhThuAllTime();

    long countTongSuKienByDateRange(LocalDate tuNgay, LocalDate denNgay);
    long countSuKienChoDuyetByDateRange(LocalDate tuNgay, LocalDate denNgay);
    long countTongNguoiDungByDateRange(LocalDate tuNgay, LocalDate denNgay);
    BigDecimal calculateDoanhThuByDateRange(LocalDate tuNgay, LocalDate denNgay);

    List<ReportResponse.KhachHangMuaNhieu> findTopKhachHangByDateRange(LocalDate tuNgay, LocalDate denNgay, Integer limit);
    List<ReportResponse.KhachHangMuaNhieu> findTopKhachHangBySuKien(String maSuKien, LocalDate tuNgay, LocalDate denNgay, Integer limit);
    ReportResponse.DoanhThuSuKien getDoanhThuAndDetailsSuKien(String maSuKien);
}
