package DuongVanBao.event.repository.impl;

import DuongVanBao.event.dto.response.ReportResponse;
import DuongVanBao.event.repository.ReportRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class ReportRepositoryImpl implements ReportRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public long countTongSuKien() {
        return entityManager.createQuery("SELECT COUNT(s) FROM SuKien s", Long.class)
                .getSingleResult();
    }

    @Override
    public long countSuKienChoDuyet() {
        return entityManager.createQuery("SELECT COUNT(s) FROM SuKien s WHERE s.hoatDong = false", Long.class)
                .getSingleResult();
    }

    @Override
    public long countTongNguoiDung() {
        return entityManager.createQuery("SELECT COUNT(n) FROM NguoiDung n", Long.class)
                .getSingleResult();
    }

    @Override
    public List<ReportResponse.DanhMucPhoBien> findDanhMucPhoBien(int month, int year) {
        TypedQuery<ReportResponse.DanhMucPhoBien> query = entityManager.createQuery("""
            SELECT NEW DuongVanBao.event.dto.response.ReportResponse$DanhMucPhoBien(
                d.tenDanhMuc,
                COUNT(l.maSuKien)
            )
            FROM DanhMucSuKien d
            JOIN LienKetSuKienDanhMuc l ON d.maDanhMuc = l.maDanhMuc
            JOIN SuKien s ON l.maSuKien = s.maSuKien
            WHERE s.hoatDong = true
            AND FUNCTION('MONTH', s.ngayTao) = :month
            AND FUNCTION('YEAR', s.ngayTao) = :year
            GROUP BY d.maDanhMuc, d.tenDanhMuc
            ORDER BY COUNT(l.maSuKien) DESC
        """, ReportResponse.DanhMucPhoBien.class);

        query.setParameter("month", month);
        query.setParameter("year", year);
        return query.setMaxResults(5).getResultList();
    }

    @Override
    public BigDecimal calculateDoanhThuThang(int month, int year) {
        TypedQuery<BigDecimal> query = entityManager.createQuery("""
            SELECT COALESCE(SUM(tt.soTien), 0)
            FROM ThanhToan tt
            JOIN DatVe dv ON tt.datVe.maDatVe = dv.maDatVe
            WHERE tt.trangThai = 'THANH_CONG'
            AND FUNCTION('MONTH', tt.thoiGianThanhToan) = :month
            AND FUNCTION('YEAR', tt.thoiGianThanhToan) = :year
        """, BigDecimal.class);

        query.setParameter("month", month);
        query.setParameter("year", year);
        BigDecimal result = query.getSingleResult();
        return result != null ? result : BigDecimal.ZERO;
    }

    @Override
    public List<ReportResponse.SuKienHot> findSuKienHotThang(int month, int year) {
        TypedQuery<ReportResponse.SuKienHot> query = entityManager.createQuery("""
            SELECT NEW DuongVanBao.event.dto.response.ReportResponse$SuKienHot(
                s.tieuDe,
                s.maSuKien,
                COUNT(v.maVe),
                SUM(lv.giaTien)
            )
            FROM SuKien s
            JOIN LoaiVe lv ON lv.suKien.maSuKien = s.maSuKien
            JOIN Ve v ON v.loaiVe.maLoaiVe = lv.maLoaiVe
            JOIN DatVe dv ON v.datVe.maDatVe = dv.maDatVe
            JOIN ThanhToan tt ON tt.datVe.maDatVe = dv.maDatVe
            WHERE tt.trangThai = 'THANH_CONG'
            AND dv.hoatDong = true
            AND FUNCTION('MONTH', tt.thoiGianThanhToan) = :month
            AND FUNCTION('YEAR', tt.thoiGianThanhToan) = :year
            GROUP BY s.maSuKien, s.tieuDe
            ORDER BY COUNT(v.maVe) DESC
        """, ReportResponse.SuKienHot.class);

        query.setParameter("month", month);
        query.setParameter("year", year);
        return query.setMaxResults(3).getResultList();
    }
}
