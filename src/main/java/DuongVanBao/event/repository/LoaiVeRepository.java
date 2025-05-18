package DuongVanBao.event.repository;

import DuongVanBao.event.model.entity.LoaiVe;
import DuongVanBao.event.model.entity.SuKien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoaiVeRepository extends JpaRepository<LoaiVe, String> {
    List<LoaiVe> findBySuKien(SuKien suKien);
    
    @Modifying
    @Query("DELETE FROM LoaiVe l WHERE l.suKien = :suKien")
    void deleteAllBySuKien(SuKien suKien);
}