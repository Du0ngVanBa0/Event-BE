package DuongVanBao.event.repository;

import DuongVanBao.event.model.entity.SuKien;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SuKienRepository extends JpaRepository<SuKien, String>, JpaSpecificationExecutor<SuKien> {
    Page<SuKien> findAllByHoatDong(boolean hoatDong, Pageable pageable);
    Page<SuKien> findByNguoiToChucMaNguoiDung(String maNguoiDung, Pageable pageable);
    Page<SuKien> findByNguoiToChucMaNguoiDungAndHoatDong(String maNguoiDung, boolean hoatDong, Pageable pageable);
}