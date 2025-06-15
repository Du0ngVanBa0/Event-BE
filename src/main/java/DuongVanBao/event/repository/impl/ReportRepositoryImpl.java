package DuongVanBao.event.repository.impl;

import DuongVanBao.event.dto.response.ReportResponse;
import DuongVanBao.event.repository.ReportRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
public class ReportRepositoryImpl implements ReportRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public long countTongSuKien() {
        return entityManager.createQuery("SELECT COUNT(s) FROM SuKien s WHERE s.hoatDong = true", Long.class)
                .getSingleResult();
    }

    @Override
    public long countSuKienChoDuyet() {
        return entityManager.createQuery("SELECT COUNT(s) FROM SuKien s WHERE s.hoatDong = false", Long.class)
                .getSingleResult();
    }

    @Override
    public long countTongNguoiDung() {
        return entityManager.createQuery("SELECT COUNT(n) FROM NguoiDung n WHERE n.hoatDong = true", Long.class)
                .getSingleResult();
    }

    @Override
    public BigDecimal calculateDoanhThuAllTime() {
        TypedQuery<BigDecimal> query = entityManager.createQuery("""
            SELECT COALESCE(SUM(tt.soTien), 0)
            FROM ThanhToan tt
            JOIN DatVe dv ON tt.datVe.maDatVe = dv.maDatVe
            WHERE tt.trangThai = 'THANH_CONG'
        """, BigDecimal.class);

        BigDecimal result = query.getSingleResult();
        return result != null ? result : BigDecimal.ZERO;
    }

    @Override
    public long countTongSuKienByDateRange(LocalDate tuNgay, LocalDate denNgay) {
        LocalDateTime start = tuNgay.atStartOfDay();
        LocalDateTime end = denNgay.plusDays(1).atStartOfDay();
        return entityManager.createQuery("""
            SELECT COUNT(s) FROM SuKien s
            WHERE s.hoatDong = true AND s.ngayTao BETWEEN :tuNgay AND :denNgay
        """, Long.class)
                .setParameter("tuNgay", start)
                .setParameter("denNgay", end)
                .getSingleResult();
    }

    @Override
    public long countSuKienChoDuyetByDateRange(LocalDate tuNgay, LocalDate denNgay) {
        LocalDateTime start = tuNgay.atStartOfDay();
        LocalDateTime end = denNgay.plusDays(1).atStartOfDay();
        return entityManager.createQuery("""
            SELECT COUNT(s) FROM SuKien s
            WHERE s.hoatDong = false
            AND s.ngayTao BETWEEN :tuNgay AND :denNgay
        """, Long.class)
                .setParameter("tuNgay", start)
                .setParameter("denNgay", end)
                .getSingleResult();
    }

    @Override
    public long countTongNguoiDungByDateRange(LocalDate tuNgay, LocalDate denNgay) {
        LocalDateTime start = tuNgay.atStartOfDay();
        LocalDateTime end = denNgay.plusDays(1).atStartOfDay();

        return entityManager.createQuery("""
            SELECT COUNT(n) FROM NguoiDung n
            WHERE n.hoatDong = true AND n.ngayTao BETWEEN :tuNgay AND :denNgay
        """, Long.class)
                .setParameter("tuNgay", start)
                .setParameter("denNgay", end)
                .getSingleResult();
    }

    @Override
    public BigDecimal calculateDoanhThuByDateRange(LocalDate tuNgay, LocalDate denNgay) {
        LocalDateTime start = tuNgay.atStartOfDay();
        LocalDateTime end = denNgay.plusDays(1).atStartOfDay();

        TypedQuery<BigDecimal> query = entityManager.createQuery("""
            SELECT COALESCE(SUM(tt.soTien), 0)
            FROM ThanhToan tt
            JOIN DatVe dv ON tt.datVe.maDatVe = dv.maDatVe
            WHERE tt.trangThai = 'THANH_CONG'
            AND tt.thoiGianThanhToan BETWEEN :tuNgay AND :denNgay
        """, BigDecimal.class);

        query.setParameter("tuNgay", start);
        query.setParameter("denNgay", end);

        BigDecimal result = query.getSingleResult();
        return result != null ? result : BigDecimal.ZERO;
    }
}
