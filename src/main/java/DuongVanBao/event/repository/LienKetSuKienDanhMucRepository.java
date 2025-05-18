package DuongVanBao.event.repository;

import DuongVanBao.event.model.LienKetSuKienDanhMucId;
import DuongVanBao.event.model.entity.LienKetSuKienDanhMuc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface LienKetSuKienDanhMucRepository extends JpaRepository<LienKetSuKienDanhMuc, LienKetSuKienDanhMucId> {
    @Modifying
    @Transactional
    @Query("DELETE FROM LienKetSuKienDanhMuc l WHERE l.id.maSuKien = :maSuKien")
    void deleteBySuKienId(String maSuKien);

    @Query("SELECT DISTINCT l FROM LienKetSuKienDanhMuc l INNER JOIN FETCH l.danhMuc WHERE l.id.maSuKien = :maSuKien")
    List<LienKetSuKienDanhMuc> findBySuKienId(String maSuKien);
}