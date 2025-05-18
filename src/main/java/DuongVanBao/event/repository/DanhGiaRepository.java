package DuongVanBao.event.repository;

import DuongVanBao.event.model.entity.DanhGia;
import DuongVanBao.event.model.entity.SuKien;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DanhGiaRepository extends JpaRepository<DanhGia, String> {
    Page<DanhGia> findBySuKien(SuKien suKien, Pageable pageable);
    boolean existsByNguoiDungMaNguoiDungAndSuKienMaSuKien(String maNguoiDung, String maSuKien);
}