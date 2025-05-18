package DuongVanBao.event.repository;

import DuongVanBao.event.dto.response.ReportResponse;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ReportRepository{
    long countTongSuKien();
    long countSuKienChoDuyet();
    long countTongNguoiDung();
    List<ReportResponse.DanhMucPhoBien> findDanhMucPhoBien(int month, int year);
    BigDecimal calculateDoanhThuThang(int month, int year);
    List<ReportResponse.SuKienHot> findSuKienHotThang(int month, int year);
}
