package DuongVanBao.event.repository.impl;

import DuongVanBao.event.dto.response.ReportResponse;
import DuongVanBao.event.model.entity.SuKien;
import DuongVanBao.event.repository.ReportRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

    @Override
    public List<ReportResponse.KhachHangMuaNhieu> findTopKhachHangByDateRange(LocalDate tuNgay, LocalDate denNgay, Integer limit) {
        LocalDateTime start = tuNgay.atStartOfDay();
        LocalDateTime end = denNgay.plusDays(1).atStartOfDay();

        TypedQuery<ReportResponse.KhachHangMuaNhieu> query = entityManager.createQuery("""
        SELECT NEW DuongVanBao.event.dto.response.ReportResponse$KhachHangMuaNhieu(
            nd.maNguoiDung,
            nd.tenHienThi,
            nd.email,
            nd.anhDaiDien,
            COUNT(v.maVe),
            SUM(lv.giaTien)
        )
        FROM NguoiDung nd
        JOIN DatVe dv ON dv.khachHang.maNguoiDung = nd.maNguoiDung
        JOIN Ve v ON v.datVe.maDatVe = dv.maDatVe
        JOIN LoaiVe lv ON v.loaiVe.maLoaiVe = lv.maLoaiVe
        JOIN ThanhToan tt ON tt.datVe.maDatVe = dv.maDatVe
        WHERE tt.trangThai = 'THANH_CONG'
        AND tt.thoiGianThanhToan BETWEEN :tuNgay AND :denNgay
        GROUP BY nd.maNguoiDung, nd.tenHienThi, nd.email, nd.anhDaiDien
        ORDER BY COUNT(v.maVe) DESC
    """, ReportResponse.KhachHangMuaNhieu.class);

        query.setParameter("tuNgay", start);
        query.setParameter("denNgay", end);

        return query.setMaxResults(limit != null ? limit : 5).getResultList();
    }

    @Override
    public List<ReportResponse.KhachHangMuaNhieu> findTopKhachHangBySuKien(String maSuKien, LocalDate tuNgay, LocalDate denNgay, Integer limit) {
        LocalDateTime start = tuNgay.atStartOfDay();
        LocalDateTime end = denNgay.plusDays(1).atStartOfDay();

        TypedQuery<ReportResponse.KhachHangMuaNhieu> query = entityManager.createQuery("""
        SELECT NEW DuongVanBao.event.dto.response.ReportResponse$KhachHangMuaNhieu(
            nd.maNguoiDung,
            nd.tenHienThi,
            nd.email,
            nd.anhDaiDien,
            COUNT(v.maVe),
            SUM(lv.giaTien)
        )
        FROM NguoiDung nd
        JOIN DatVe dv ON dv.khachHang.maNguoiDung = nd.maNguoiDung
        JOIN Ve v ON v.datVe.maDatVe = dv.maDatVe
        JOIN LoaiVe lv ON v.loaiVe.maLoaiVe = lv.maLoaiVe
        JOIN SuKien s ON lv.suKien.maSuKien = s.maSuKien
        JOIN ThanhToan tt ON tt.datVe.maDatVe = dv.maDatVe
        WHERE tt.trangThai = 'THANH_CONG'
        AND s.maSuKien = :maSuKien
        AND tt.thoiGianThanhToan BETWEEN :tuNgay AND :denNgay
        GROUP BY nd.maNguoiDung, nd.tenHienThi, nd.email, nd.anhDaiDien
        ORDER BY COUNT(v.maVe) DESC
    """, ReportResponse.KhachHangMuaNhieu.class);

        query.setParameter("maSuKien", maSuKien);
        query.setParameter("tuNgay", start);
        query.setParameter("denNgay", end);

        return query.setMaxResults(limit != null ? limit : 5).getResultList();
    }

    @Override
    public ReportResponse.DoanhThuSuKien getDoanhThuAndDetailsSuKien(String maSuKien) {
        SuKien suKien = entityManager.find(SuKien.class, maSuKien);
        if (suKien == null) {
            return null;
        }

        TypedQuery<BigDecimal> revenueQuery = entityManager.createQuery("""
        SELECT COALESCE(SUM(lv.giaTien), 0)
        FROM Ve v
        JOIN v.loaiVe lv
        JOIN v.datVe dv
        JOIN ThanhToan tt ON tt.datVe.maDatVe = dv.maDatVe
        WHERE lv.suKien.maSuKien = :maSuKien
        AND tt.trangThai = 'THANH_CONG'
    """, BigDecimal.class);
        revenueQuery.setParameter("maSuKien", maSuKien);
        BigDecimal tongDoanhThu = revenueQuery.getSingleResult();
        if (tongDoanhThu == null) tongDoanhThu = BigDecimal.ZERO;

        TypedQuery<Long> soldTicketsQuery = entityManager.createQuery("""
        SELECT COUNT(v.maVe)
        FROM Ve v
        JOIN v.loaiVe lv
        JOIN v.datVe dv
        JOIN ThanhToan tt ON tt.datVe.maDatVe = dv.maDatVe
        WHERE lv.suKien.maSuKien = :maSuKien
        AND tt.trangThai = 'THANH_CONG'
    """, Long.class);
        soldTicketsQuery.setParameter("maSuKien", maSuKien);
        Long soVeDaBan = soldTicketsQuery.getSingleResult();

        TypedQuery<Long> totalTicketsQuery = entityManager.createQuery("""
        SELECT COALESCE(SUM(lv.soLuong), 0)
        FROM LoaiVe lv
        WHERE lv.suKien.maSuKien = :maSuKien
    """, Long.class);
        totalTicketsQuery.setParameter("maSuKien", maSuKien);
        long tongSoVe = totalTicketsQuery.getSingleResult();
        long soVeConLai = tongSoVe - soVeDaBan;

        ReportResponse.DoanhThuSuKien result = new ReportResponse.DoanhThuSuKien();
        result.setMaSuKien(maSuKien);
        result.setTieuDe(suKien.getTieuDe());
        result.setThoiGianBatDau(suKien.getThoiGianBatDau());
        result.setThoiGianKetThuc(suKien.getThoiGianKetThuc());
        result.setTongDoanhThu(tongDoanhThu);
        result.setSoVeDaBan(soVeDaBan);
        result.setSoVeConLai(soVeConLai);

        return result;
    }
}
